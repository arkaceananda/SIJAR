package com.example.sijar

import android.app.Application
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.SessionManager
import com.example.sijar.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SIJARApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inisialisasi ApiClient & SessionManager
        val sessionManager = SessionManager.getInstance(this)
        ApiClient.init(sessionManager)

        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@SIJARApplication)
            modules(appModule)
        }
    }
}
