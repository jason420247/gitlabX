package com.sozonov.gitlabx

import android.app.Application
import android.util.Log
import com.sozonov.gitlabx.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GitlabXApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e(thread.name, exception.message, exception)
        }

        startKoin {
            androidLogger()
            androidContext(this@GitlabXApplication)
            modules(appModule)
        }
    }
}