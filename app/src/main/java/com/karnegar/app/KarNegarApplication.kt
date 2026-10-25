package com.karnegar.app

import android.app.Application
import com.karnegar.app.reminders.NotificationHelper

class KarNegarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
