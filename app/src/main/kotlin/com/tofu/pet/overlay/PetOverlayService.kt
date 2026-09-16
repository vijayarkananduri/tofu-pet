package com.tofu.pet.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
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
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.NotificationCompat
import com.tofu.pet.R
import kotlin.math.sqrt

class PetOverlayService : Service(), SensorEventListener {

    private lateinit var windowManager: WindowManager
    private lateinit var webView: WebView
    private lateinit var sensorManager: SensorManager
    private lateinit var vibrator: Vibrator
    private lateinit var handler: Handler

    private var lastX = 0f
    private var lastY = 0f
    private var petX = 0f
    private var petY = 0f
    private var lastShakeTime = 0L
    private var accumulatedRotation = 0f
    private var lastGyroZ = 0f

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        handler = Handler(Looper.getMainLooper())

        createNotificationChannel()
        startForeground(1, createNotification())
        setupWebView()
        registerSensors()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun setupWebView() {
        webView = WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
            }
            webViewClient = WebViewClient()
            setBackgroundColor(0x00000000) // Transparent
            addJavascriptInterface(AndroidBridge(), "AndroidBridge")
        }

        // Load pet HTML from assets
        webView.loadUrl("file:///android_asset/tofu.html")

        val params = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            gravity = Gravity.TOP or Gravity.START
            width = 200
            height = 120
            x = 100
            y = 100
        }

        windowManager.addView(webView, params)

        // Enable touch handling
        webView.setOnTouchListener { _, event ->
            handleTouchEvent(event)
            true
        }
    }

    private fun handleTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                vibrate(10)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY

                if (sqrt(dx * dx + dy * dy) > 10) {
                    petX += dx
                    petY += dy
                    updatePetPosition(petX.toInt(), petY.toInt())
                    lastX = event.rawX
                    lastY = event.rawY
                }
            }
            MotionEvent.ACTION_UP -> {
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY
                if (sqrt(dx * dx + dy * dy) < 10) {
                    // Tap detected
                    webView.evaluateJavascript("window.Pet.onTap()") { }
                    vibrate(20)
                }
            }
        }
    }

    private fun updatePetPosition(x: Int, y: Int) {
        val params = webView.layoutParams as WindowManager.LayoutParams
        params.x = x
        params.y = y
        windowManager.updateViewLayout(webView, params)
    }

    private fun registerSensors() {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                handleAccelerometerEvent(event.values)
            }
            Sensor.TYPE_GYROSCOPE -> {
                handleGyroscopeEvent(event.values)
            }
        }
    }

    private fun handleAccelerometerEvent(values: FloatArray) {
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val force = sqrt(x * x + y * y + z * z) - 9.81f
        val level = (force / 2).toInt().coerceIn(1, 10)

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShakeTime > 2000) {
            if (level in 1..5) {
                webView.evaluateJavascript("window.Pet.onShake(${level})") { }
                vibrate(30)
            } else if (level in 6..10) {
                webView.evaluateJavascript("window.Pet.onShake(${level})") { }
                vibrate(50)
            }
            lastShakeTime = currentTime
        }
    }

    private fun handleGyroscopeEvent(values: FloatArray) {
        val gyroZ = values[2]
        accumulatedRotation += gyroZ

        if (kotlin.math.abs(accumulatedRotation) > 4 * Math.PI) {
            webView.evaluateJavascript("window.Pet.onSpin()") { }
            accumulatedRotation = 0f
            vibrate(25)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun vibrate(ms: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(
                ms,
                android.os.VibrationEffect.DEFAULT_AMPLITUDE
            ))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ms)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "tofu_service",
                "Tofu Pet Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "tofu_service")
            .setContentTitle("Tofu is running")
            .setContentText("Your pet is floating on your screen")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        if (::webView.isInitialized) {
            windowManager.removeView(webView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    inner class AndroidBridge {
        @android.webkit.JavascriptInterface
        fun vibrate(ms: Long) {
            this@PetOverlayService.vibrate(ms)
        }

        @android.webkit.JavascriptInterface
        fun reportBounds(x: Int, y: Int, w: Int, h: Int) {
            // Handle pet bounds for click detection
        }

        @android.webkit.JavascriptInterface
        fun openApp() {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(intent)
        }

        @android.webkit.JavascriptInterface
        fun requestTts(text: String) {
            // TODO: Implement TTS
        }

        @android.webkit.JavascriptInterface
        fun checkTask(taskId: String) {
            // TODO: Implement task checking
        }

        @android.webkit.JavascriptInterface
        fun snoozeTask(taskId: String) {
            // TODO: Implement task snoozing
        }
    }
}
