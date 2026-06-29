package com.example.widget

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object LunarWidgetScheduler {
    /**
     * Schedules a periodic task to run every 12 hours. This ensures that the widget
     * is kept up-to-date and refreshed automatically.
     */
    fun scheduleDailyUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<LunarWidgetWorker>(
            12, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "LunarWidgetDailyUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
