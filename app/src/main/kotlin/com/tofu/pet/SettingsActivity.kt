package com.tofu.pet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
from com.tofu.pet.ui.theme.TofuPetTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TofuPetTheme {
                SettingsScreen()
            }
        }
    }

    @Composable
    fun SettingsScreen() {
        var tiltSensitivity by remember { mutableStateOf(0.5f) }
        var shakeSensitivity by remember { mutableStateOf(0.5f) }
        var soundEnabled by remember { mutableStateOf(true) }
        var userName by remember { mutableStateOf("") }
        var petSize by remember { mutableStateOf(1f) }
        var defaultSnoozeTime by remember { mutableStateOf(5) }
        var isMuted by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5E6C8))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C3A1E)
                )

                SettingItem("Tilt Sensitivity") {
                    Slider(
                        value = tiltSensitivity,
                        onValueChange = { tiltSensitivity = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                SettingItem("Shake Sensitivity") {
                    Slider(
                        value = shakeSensitivity,
                        onValueChange = { shakeSensitivity = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                SettingItem("Pet Size") {
                    Slider(
                        value = petSize,
                        onValueChange = { petSize = it },
                        valueRange = 0.5f..2f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                SettingItem("Your Name") {
                    TextField(
                        value = userName,
                        onValueChange = { userName = it },
                        placeholder = { Text("Optional") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                SettingItem("Default Snooze Time") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 30).forEach { time ->
                            Button(
                                onClick = { defaultSnoozeTime = time },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (defaultSnoozeTime == time) {
                                        Color(0xFF5C3A1E)
                                    } else {
                                        Color(0xFFE8D4A8)
                                    }
                                )
                            ) {
                                Text("${time}m")
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Enabled")
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Muted")
                    Switch(
                        checked = isMuted,
                        onCheckedChange = { isMuted = it }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { finish() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Back")
                }
            }
        }
    }

    @Composable
    fun SettingItem(
        title: String,
        content: @Composable () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C3A1E)
            )
            content()
        }
    }
}
