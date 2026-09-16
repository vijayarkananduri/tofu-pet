package com.tofu.pet.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        val taskId = intent.getStringExtra("taskId") ?: return
        val title = intent.getStringExtra("title") ?: return

        // Trigger reminder notification and overlay update
        val overlayIntent = Intent(context, com.tofu.pet.overlay.PetOverlayService::class.java).apply {
            action = "com.tofu.pet.SHOW_REMINDER"
            putExtra("taskId", taskId)
            putExtra("title", title)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(overlayIntent)
        } else {
            context.startService(overlayIntent)
        }
    }
}
