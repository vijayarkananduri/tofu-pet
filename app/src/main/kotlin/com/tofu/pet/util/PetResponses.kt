package com.tofu.pet.util

object PetResponses {
    val warmReplies = listOf(
        "You're doing good.",
        "I like this.",
        "Hey you.",
        "Good.",
        "You've been nice lately.",
        "I'm proud of you.",
        "Keep it up!",
        "That's awesome."
    )

    val neutralReplies = listOf(
        "Okay.",
        "Hm.",
        "Hi.",
        "Sure.",
        "...",
        "Got it.",
        "Thanks.",
        "Noted."
    )

    val coldReplies = listOf(
        "You're doing it again.",
        "Again?",
        "Hm.",
        "...",
        "I noticed.",
        "Whatever.",
        "OK.",
        "Right."
    )

    val taskDoneReplies = listOf(
        "Great job!",
        "Nice work!",
        "Awesome!",
        "Well done.",
        "Good job."
    )

    val praiseReplies = listOf(
        "You're on fire!",
        "I'm really proud of you.",
        "This is amazing.",
        "You're disciplined.",
        "I love this about you."
    )

    val snoozeReplies = listOf(
        "Okay, 5 more minutes!",
        "Fine... but hurry.",
        "This is the LAST time."
    )

    fun getRandomReply(mood: String): String {
        return when (mood) {
            "warm" -> warmReplies.random()
            "cold" -> coldReplies.random()
            else -> neutralReplies.random()
        }
    }

    fun parseVoiceCommand(command: String): String? {
        val normalized = command.lowercase().trim()
        return when {
            normalized.contains("hello") || normalized.contains("hi") -> "wave"
            normalized.contains("how many") || normalized.contains("what's up") -> "count"
            normalized.contains("show me") || normalized.contains("open") -> "open_app"
            normalized.contains("done") -> "done"
            normalized.contains("snooze") -> "snooze"
            normalized.contains("what's task") || normalized.contains("read") -> "read_task"
            normalized.contains("mute") || normalized.contains("shush") -> "mute"
            normalized.contains("unmute") || normalized.contains("talk") -> "unmute"
            else -> null
        }
    }
}
