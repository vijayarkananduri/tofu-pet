package com.tofu.pet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tofu.pet.ui.theme.TofuPetTheme

private val TofuCream = Color(0xFFFFF8EA)
private val TofuBrown = Color(0xFF5C3A1E)
private val TofuGold = Color(0xFFE8B86D)
private val TofuSoft = Color(0xFFFFE7C2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TofuPetTheme { MainScreen() } }
    }

    @Composable
    private fun MainScreen() {
        var showOnboarding by remember { mutableStateOf(true) }
        var overlayGranted by remember { mutableStateOf(false) }
        var micGranted by remember { mutableStateOf(false) }
        var notificationsGranted by remember { mutableStateOf(true) }

        fun refreshPermissions() {
            overlayGranted = Settings.canDrawOverlays(this@MainActivity)
            micGranted = hasPermission(Manifest.permission.RECORD_AUDIO)
            notificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        }

        LaunchedEffect(Unit) { refreshPermissions() }
        Box(Modifier.fillMaxSize().background(TofuCream)) {
            if (showOnboarding && (!overlayGranted || !micGranted)) {
                OnboardingScreen(overlayGranted, micGranted, notificationsGranted, { refreshPermissions() }) {
                    refreshPermissions()
                    if (Settings.canDrawOverlays(this@MainActivity) && hasPermission(Manifest.permission.RECORD_AUDIO)) showOnboarding = false
                }
            } else HomeScreen()
        }
    }

    @Composable
    private fun OnboardingScreen(
        overlayGranted: Boolean,
        micGranted: Boolean,
        notificationsGranted: Boolean,
        onRefresh: () -> Unit,
        onContinue: () -> Unit
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(eyebrow = "WELCOME TO TOFU", title = "Meet your tiny reminder friend", subtitle = "Tofu lives gently on top of your phone and helps you remember what matters.")
            Card(colors = CardDefaults.cardColors(containerColor = TofuSoft), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("A little permission, then a lot of personality.", fontWeight = FontWeight.Bold, color = TofuBrown, fontSize = 18.sp)
                    Text("Overlay permission lets Tofu float above other apps. Microphone access enables voice commands. Notifications are optional but useful for reminders.", color = TofuBrown.copy(alpha = .8f), lineHeight = 21.sp)
                }
            }
            PermissionCard("Display over other apps", "Required for the floating pet", overlayGranted) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            }
            PermissionCard("Microphone access", "Required for voice commands", micGranted) { requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_MIC) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) PermissionCard("Notifications", "Recommended for reminders", notificationsGranted) { requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS) }
            Spacer(Modifier.height(4.dp))
            Button(onClick = { onRefresh(); onContinue() }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = TofuBrown)) { Text("Continue", fontSize = 16.sp) }
            Text("You can change permissions later in Android Settings.", modifier = Modifier.fillMaxWidth(), color = TofuBrown.copy(alpha = .6f), fontSize = 12.sp)
        }
    }

    @Composable
    private fun PermissionCard(title: String, description: String, granted: Boolean, onClick: () -> Unit) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold, color = TofuBrown); Text(description, color = TofuBrown.copy(alpha = .65f), fontSize = 12.sp) }
                if (granted) Text("Ready", color = Color(0xFF357A4A), fontWeight = FontWeight.Bold) else OutlinedButton(onClick = onClick, shape = RoundedCornerShape(12.dp)) { Text("Allow") }
            }
        }
    }

    @Composable
    private fun HomeScreen() {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Header("TOFU PET", "Tofu is ready", "A soft little friend who remembers without nagging.")
            Card(colors = CardDefaults.cardColors(containerColor = TofuBrown), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Your pet is waiting", color = Color.White.copy(alpha = .75f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Bring Tofu onto your screen and let it react to your day.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
                    Button(onClick = { startOverlayService() }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = TofuGold, contentColor = TofuBrown)) { Text("Launch Pet Overlay", fontWeight = FontWeight.Bold) }
                }
            }
            InfoCard("What Tofu can do", "Tap, drag, shake, tilt, speak, and respond to reminders. Long-press Tofu to start a voice command.")
            InfoCard("Make it yours", "Adjust pet size, sensitivity, sound, mute, and snooze preferences in Settings.")
            OutlinedButton(onClick = { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) { Text("Open Settings", color = TofuBrown) }
            Text("Tofu works offline. Keep the notification visible while the overlay is active.", color = TofuBrown.copy(alpha = .6f), fontSize = 12.sp, lineHeight = 17.sp)
        }
    }

    @Composable
    private fun Header(eyebrow: String, title: String, subtitle: String) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(eyebrow, color = TofuGold.copy(alpha = .95f), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(title, color = TofuBrown, fontSize = 30.sp, fontWeight = FontWeight.Bold, lineHeight = 35.sp)
            Text(subtitle, color = TofuBrown.copy(alpha = .7f), fontSize = 16.sp, lineHeight = 22.sp)
        }
    }

    @Composable
    private fun InfoCard(title: String, text: String) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text(title, color = TofuBrown, fontWeight = FontWeight.Bold, fontSize = 17.sp); Text(text, color = TofuBrown.copy(alpha = .7f), lineHeight = 20.sp) } }
    }

    private fun startOverlayService() {
        if (!Settings.canDrawOverlays(this)) return
        val intent = Intent(this, com.tofu.pet.overlay.PetOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
    }

    private fun hasPermission(permission: String): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    companion object { private const val REQUEST_MIC = 101; private const val REQUEST_NOTIFICATIONS = 102 }
}
