package com.tofu.pet.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.NotificationCompat
import com.tofu.pet.R
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

class PetOverlayService : Service(), SensorEventListener {
    private lateinit var windowManager: WindowManager
    private lateinit var webView: WebView
    private lateinit var sensorManager: SensorManager
    private lateinit var vibrator: Vibrator
    private lateinit var handler: Handler
    private var textToSpeech: TextToSpeech? = null
    private var lastX = 0f
    private var lastY = 0f
    private var petX = 20
    private var petY = 72
    private var lastShakeTime = 0L
    private var accumulatedRotation = 0.0

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        handler = Handler(Looper.getMainLooper())
        textToSpeech = TextToSpeech(this) { if (it == TextToSpeech.SUCCESS) textToSpeech?.language = Locale.getDefault() }
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        setupWebView()
        registerSensors()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_SHOW_REMINDER) {
            val taskId = intent.getStringExtra("taskId") ?: ""
            val title = intent.getStringExtra("title") ?: "You have a task"
            handler.post { if (::webView.isInitialized) webView.evaluateJavascript("window.Pet.onReminder(${js(taskId)},${js(title)},1)", null) }
        }
        return START_STICKY
    }

    private fun setupWebView() {
        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            webViewClient = WebViewClient()
            setBackgroundColor(0x00000000)
            addJavascriptInterface(AndroidBridge(), "AndroidBridge")
        }
        webView.loadUrl("file:///android_asset/tofu.html")
        val params = WindowManager.LayoutParams(
            320, 300, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = petX; y = petY }
        windowManager.addView(webView, params)
        webView.setOnTouchListener { _, event -> handleTouchEvent(event); true }
    }

    private fun handleTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { lastX = event.rawX; lastY = event.rawY; vibrate(10) }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - lastX; val dy = event.rawY - lastY
                if (sqrt(dx * dx + dy * dy) > 10) { petX += dx.toInt(); petY += dy.toInt(); updatePetPosition(); lastX = event.rawX; lastY = event.rawY }
            }
            MotionEvent.ACTION_UP -> {
                if (sqrt((event.rawX - lastX) * (event.rawX - lastX) + (event.rawY - lastY) * (event.rawY - lastY)) < 20) {
                    webView.evaluateJavascript("window.Pet.onTap()", null); vibrate(20)
                }
            }
        }
    }

    private fun updatePetPosition() {
        if (!::webView.isInitialized) return
        val params = webView.layoutParams as WindowManager.LayoutParams
        val metrics = resources.displayMetrics
        petX = petX.coerceIn(0, (metrics.widthPixels - params.width).coerceAtLeast(0))
        petY = petY.coerceIn(0, (metrics.heightPixels - params.height).coerceAtLeast(0))
        params.x = petX; params.y = petY
        windowManager.updateViewLayout(webView, params)
    }

    private fun registerSensors() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val force = abs(sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]) - 9.81f)
                val level = (force / 2).toInt().coerceIn(1, 10)
                if (force > 2 && System.currentTimeMillis() - lastShakeTime > 2000) {
                    webView.evaluateJavascript("window.Pet.onShake($level)", null); vibrate(30); lastShakeTime = System.currentTimeMillis()
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                accumulatedRotation += event.values[2].toDouble() * 0.02
                if (abs(accumulatedRotation) > 4 * Math.PI) { webView.evaluateJavascript("window.Pet.onSpin()", null); accumulatedRotation = 0.0; vibrate(25) }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun vibrate(ms: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(android.os.VibrationEffect.createOneShot(ms, android.os.VibrationEffect.DEFAULT_AMPLITUDE)) else @Suppress("DEPRECATION") vibrator.vibrate(ms)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(CHANNEL_ID, "Tofu Pet", NotificationManager.IMPORTANCE_LOW))
        }
    }

    private fun createNotification(): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Tofu is running").setContentText("Your pet is floating on your screen")
        .setSmallIcon(R.mipmap.ic_launcher_foreground).setOngoing(true).setPriority(NotificationCompat.PRIORITY_LOW).build()

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        if (::webView.isInitialized) { windowManager.removeView(webView); webView.destroy() }
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    inner class AndroidBridge {
        @JavascriptInterface fun vibrate(ms: Long) = this@PetOverlayService.vibrate(ms)
        @JavascriptInterface fun reportBounds(x: Int, y: Int, w: Int, h: Int) = Unit
        @JavascriptInterface fun openApp() { packageManager.getLaunchIntentForPackage(packageName)?.let { it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(it) } }
        @JavascriptInterface fun requestTts(text: String) { handler.post { textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tofu") } }
        @JavascriptInterface fun checkTask(taskId: String) = Unit
        @JavascriptInterface fun snoozeTask(taskId: String) = Unit
    }

    private fun js(value: String): String = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""

    companion object {
        const val ACTION_SHOW_REMINDER = "com.tofu.pet.SHOW_REMINDER"
        private const val CHANNEL_ID = "tofu_service"
        private const val NOTIFICATION_ID = 1
    }
}
