package com.tofu.pet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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

private val Cream = Color(0xFFFFF8EA)
private val Brown = Color(0xFF5C3A1E)
private val Gold = Color(0xFFE8B86D)

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { TofuPetTheme { SettingsScreen() } } }

    @Composable
    private fun SettingsScreen() {
        var tilt by remember { mutableStateOf(.5f) }
        var shake by remember { mutableStateOf(.5f) }
        var size by remember { mutableStateOf(1f) }
        var name by remember { mutableStateOf("") }
        var snooze by remember { mutableStateOf(5) }
        var sound by remember { mutableStateOf(true) }
        var muted by remember { mutableStateOf(false) }
        var autoMute by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxSize().background(Cream).verticalScroll(rememberScrollState()).statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) { Text("TOFU PET", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp); Text("Settings", color = Brown, fontSize = 30.sp, fontWeight = FontWeight.Bold) }
                TextButton(onClick = { finish() }) { Text("Done", color = Brown, fontWeight = FontWeight.Bold) }
            }
            Text("Tune how Tofu feels, moves, and reminds you.", color = Brown.copy(alpha = .7f), fontSize = 16.sp)
            SettingCard("Movement") {
                SliderSetting("Tilt sensitivity", "How much Tofu reacts to tilt", tilt, { tilt = it })
                SliderSetting("Shake sensitivity", "Higher means a stronger shake is needed", shake, { shake = it })
                SliderSetting("Pet size", "From small companion to big personality", size, { size = it }, .5f..2f)
            }
            SettingCard("Personal touch") {
                Text("Your name", color = Brown, fontWeight = FontWeight.Bold)
                TextField(value = name, onValueChange = { name = it }, placeholder = { Text("Optional") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            }
            SettingCard("Reminders") {
                Text("Default snooze time", color = Brown, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(5, 10, 15, 30).forEach { value -> Button(onClick = { snooze = value }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (snooze == value) Brown else Color(0xFFFFE7C2), contentColor = if (snooze == value) Color.White else Brown)) { Text("${value}m") } } }
            }
            SettingCard("Sound & accessibility") {
                Toggle("Sound enabled", "Reactions and spoken replies", sound) { sound = it }
                Toggle("Muted", "Silence Tofu without changing other settings", muted) { muted = it }
                Toggle("Auto-mute on silent", "Follow the phone's silent mode", autoMute) { autoMute = it }
            }
            Spacer(Modifier.height(4.dp))
            Button(onClick = { finish() }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Brown)) { Text("Save and close", fontWeight = FontWeight.Bold) }
            Text("Changes apply the next time Tofu reacts.", color = Brown.copy(alpha = .55f), fontSize = 12.sp)
        }
    }

    @Composable private fun SettingCard(title: String, content: @Composable () -> Unit) { Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text(title, color = Brown, fontWeight = FontWeight.Bold, fontSize = 18.sp); content() } } }
    @Composable private fun SliderSetting(title: String, subtitle: String, value: Float, onChange: (Float) -> Unit, range: ClosedFloatingPointRange<Float> = 0f..1f) { Column(verticalArrangement = Arrangement.spacedBy(2.dp)) { Text(title, color = Brown, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Brown.copy(alpha = .6f), fontSize = 12.sp); Slider(value = value, onValueChange = onChange, valueRange = range) } }
    @Composable private fun Toggle(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Column(Modifier.weight(1f)) { Text(title, color = Brown, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Brown.copy(alpha = .6f), fontSize = 12.sp) }; Switch(checked = checked, onCheckedChange = onChange) } }
}
