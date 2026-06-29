package com.example

import android.app.Application
import com.example.widget.LunarWidgetScheduler

class LunarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Automatically schedule and refresh daily activities and lunar date updates
        LunarWidgetScheduler.scheduleDailyUpdate(this)
    }
}
