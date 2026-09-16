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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TofuPetTheme { MainScreen() }
        }
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
            notificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        }

        LaunchedEffect(Unit) { refreshPermissions() }

        if (showOnboarding && (!overlayGranted || !micGranted)) {
            OnboardingScreen(
                overlayGranted = overlayGranted,
                micGranted = micGranted,
                notificationsGranted = notificationsGranted,
                onRefresh = { refreshPermissions() },
                onContinue = {
                    refreshPermissions()
                    if (Settings.canDrawOverlays(this@MainActivity) && hasPermission(Manifest.permission.RECORD_AUDIO)) {
                        showOnboarding = false
                    }
                }
            )
        } else {
            HomeScreen()
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
        Box(Modifier.fillMaxSize().background(Color(0xFFF5E6C8)), contentAlignment = Alignment.Center) {
            Column(
                Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Meet Tofu", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C3A1E))
                Spacer(Modifier.height(16.dp))
                Text("A soft little friend who remembers", fontSize = 18.sp, color = Color(0xFF5C3A1E))
                Spacer(Modifier.height(32.dp))

                if (!overlayGranted) {
                    PermissionButton("Display over other apps", "Required for the floating pet") {
                        startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                    }
                    Spacer(Modifier.height(16.dp))
                }
                if (!micGranted) {
                    PermissionButton("Microphone access", "Required for voice commands") {
                        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_MIC)
                    }
                    Spacer(Modifier.height(16.dp))
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationsGranted) {
                    PermissionButton("Notifications", "Recommended for reminders") {
                        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS)
                    }
                    Spacer(Modifier.height(16.dp))
                }
                Spacer(Modifier.height(24.dp))
                Button(onClick = { onRefresh(); onContinue() }, Modifier.fillMaxWidth().height(48.dp)) {
                    Text("Continue")
                }
            }
        }
    }

    @Composable
    private fun PermissionButton(title: String, description: String, onClick: () -> Unit) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, fontSize = 12.sp)
            }
        }
    }

    @Composable
    private fun HomeScreen() {
        Box(Modifier.fillMaxSize().background(Color(0xFFF5E6C8)), contentAlignment = Alignment.Center) {
            Column(Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tofu is ready!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C3A1E))
                Spacer(Modifier.height(32.dp))
                Button(onClick = { startOverlayService() }, Modifier.fillMaxWidth().height(48.dp)) { Text("Launch Pet Overlay") }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }, Modifier.fillMaxWidth().height(48.dp)) { Text("Settings") }
            }
        }
    }

    private fun startOverlayService() {
        if (!Settings.canDrawOverlays(this)) return
        val intent = Intent(this, com.tofu.pet.overlay.PetOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
    }

    private fun hasPermission(permission: String): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val REQUEST_MIC = 101
        private const val REQUEST_NOTIFICATIONS = 102
    }
}
