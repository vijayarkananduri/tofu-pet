package com.tofu.pet.model

data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val dueTime: Long,
    val isCompleted: Boolean = false,
    val snoozeCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class PetMood(
    val level: String, // "warm", "neutral", "cold"
    val moodScore: Int, // -3 to +3
    val trustScore: Int, // 0 to 3
    val completedToday: Int
)
