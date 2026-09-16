package com.tofu.pet.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tofu_prefs")

class PreferencesRepository(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private val TILT_SENSITIVITY = floatPreferencesKey("tilt_sensitivity")
        private val SHAKE_SENSITIVITY = floatPreferencesKey("shake_sensitivity")
        private val PET_SIZE = floatPreferencesKey("pet_size")
        private val DEFAULT_SNOOZE_TIME = intPreferencesKey("default_snooze_time")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val IS_MUTED = booleanPreferencesKey("is_muted")
        private val AUTO_MUTE_ON_SILENT = booleanPreferencesKey("auto_mute_on_silent")
        private val MOOD_SCORE = intPreferencesKey("mood_score")
        private val TRUST_SCORE = intPreferencesKey("trust_score")
        private val COMPLETED_TODAY = intPreferencesKey("completed_today")
    }

    val userName: Flow<String> = dataStore.data.map { it[USER_NAME] ?: "" }
    val tiltSensitivity: Flow<Float> = dataStore.data.map { it[TILT_SENSITIVITY] ?: 0.5f }
    val shakeSensitivity: Flow<Float> = dataStore.data.map { it[SHAKE_SENSITIVITY] ?: 0.5f }
    val petSize: Flow<Float> = dataStore.data.map { it[PET_SIZE] ?: 1f }
    val defaultSnoozeTime: Flow<Int> = dataStore.data.map { it[DEFAULT_SNOOZE_TIME] ?: 5 }
    val soundEnabled: Flow<Boolean> = dataStore.data.map { it[SOUND_ENABLED] ?: true }
    val isMuted: Flow<Boolean> = dataStore.data.map { it[IS_MUTED] ?: false }
    val autoMuteOnSilent: Flow<Boolean> = dataStore.data.map { it[AUTO_MUTE_ON_SILENT] ?: false }
    val moodScore: Flow<Int> = dataStore.data.map { it[MOOD_SCORE] ?: 0 }
    val trustScore: Flow<Int> = dataStore.data.map { it[TRUST_SCORE] ?: 3 }
    val completedToday: Flow<Int> = dataStore.data.map { it[COMPLETED_TODAY] ?: 0 }

    suspend fun updateUserName(name: String) {
        dataStore.edit { it[USER_NAME] = name }
    }

    suspend fun updateTiltSensitivity(value: Float) {
        dataStore.edit { it[TILT_SENSITIVITY] = value }
    }

    suspend fun updateShakeSensitivity(value: Float) {
        dataStore.edit { it[SHAKE_SENSITIVITY] = value }
    }

    suspend fun updatePetSize(value: Float) {
        dataStore.edit { it[PET_SIZE] = value }
    }

    suspend fun updateDefaultSnoozeTime(minutes: Int) {
        dataStore.edit { it[DEFAULT_SNOOZE_TIME] = minutes }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[SOUND_ENABLED] = enabled }
    }

    suspend fun updateIsMuted(muted: Boolean) {
        dataStore.edit { it[IS_MUTED] = muted }
    }

    suspend fun updateAutoMuteOnSilent(enabled: Boolean) {
        dataStore.edit { it[AUTO_MUTE_ON_SILENT] = enabled }
    }

    suspend fun updateMoodScore(score: Int) {
        dataStore.edit { it[MOOD_SCORE] = score.coerceIn(-3, 3) }
    }

    suspend fun updateTrustScore(score: Int) {
        dataStore.edit { it[TRUST_SCORE] = score.coerceIn(0, 3) }
    }

    suspend fun updateCompletedToday(count: Int) {
        dataStore.edit { it[COMPLETED_TODAY] = count }
    }
}
