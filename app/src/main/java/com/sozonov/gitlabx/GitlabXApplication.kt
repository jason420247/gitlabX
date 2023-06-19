package com.sozonov.gitlabx

import android.app.Application
import com.sozonov.gitlabx.di.appModule
import com.sozonov.gitlabx.di.httpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GitlabXApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GitlabXApplication)
            modules(appModule, httpClient)
        }
    }
}