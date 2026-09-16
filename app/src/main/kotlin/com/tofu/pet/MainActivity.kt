package com.tofu.pet

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import android.provider.Settings
from com.tofu.pet.ui.theme.TofuPetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TofuPetTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        var showOnboarding by remember { mutableStateOf(true) }
        var overlayPermissionGranted by remember { mutableStateOf(false) }
        var micPermissionGranted by remember { mutableStateOf(false) }
        var notificationPermissionGranted by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            overlayPermissionGranted = Settings.canDrawOverlays(this@MainActivity)
            micPermissionGranted = hasPermission(android.Manifest.permission.RECORD_AUDIO)
            notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                hasPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                true
            }
        }

        if (showOnboarding && (!overlayPermissionGranted || !micPermissionGranted)) {
            OnboardingScreen(
                overlayPermissionGranted = overlayPermissionGranted,
                micPermissionGranted = micPermissionGranted,
                notificationPermissionGranted = notificationPermissionGranted,
                onPermissionsGranted = {
                    overlayPermissionGranted = Settings.canDrawOverlays(this@MainActivity)
                    micPermissionGranted = hasPermission(android.Manifest.permission.RECORD_AUDIO)
                    notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        hasPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        true
                    }
                },
                onCompleted = { showOnboarding = false }
            )
        } else {
            HomeScreen()
        }
    }

    @Composable
    fun OnboardingScreen(
        overlayPermissionGranted: Boolean,
        micPermissionGranted: Boolean,
        notificationPermissionGranted: Boolean,
        onPermissionsGranted: () -> Unit,
        onCompleted: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5E6C8)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Meet Tofu",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C3A1E)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "A soft little friend who remembers",
                    fontSize = 18.sp,
                    color = Color(0xFF5C3A1E)
                )
                Spacer(modifier = Modifier.height(32.dp))

                if (!overlayPermissionGranted) {
                    PermissionButton(
                        title = "Display over other apps",
                        description = "Tofu needs permission to float over your screen",
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${packageName}")
                            )
                            startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (!micPermissionGranted) {
                    PermissionButton(
                        title = "Microphone access",
                        description = "Tofu listens to your voice commands",
                        onClick = {
                            requestPermissions(
                                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                                101
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (!notificationPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionButton(
                        title = "Notifications",
                        description = "Tofu sends reminder notifications",
                        onClick = {
                            requestPermissions(
                                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                102
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onPermissionsGranted()
                        if (overlayPermissionGranted && micPermissionGranted && notificationPermissionGranted) {
                            onCompleted()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Continue")
                }
            }
        }
    }

    @Composable
    fun PermissionButton(
        title: String,
        description: String,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, fontSize = 12.sp)
            }
        }
    }

    @Composable
    fun HomeScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5E6C8)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tofu is ready!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C3A1E)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { startOverlayService() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Launch Pet Overlay")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { openSettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Settings")
                }
            }
        }
    }

    private fun startOverlayService() {
        val intent = Intent(this, com.tofu.pet.overlay.PetOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun openSettings() {
        startActivity(Intent(this, com.tofu.pet.SettingsActivity::class.java))
    }

    private fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
