package com.sozonov.gitlabx

import android.app.Application
import android.util.Log
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.di.rootModule
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.user.dal.IUserCache
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get
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
            modules(rootModule)
        }

        runBlocking {
            val authService = get<AuthService>()
            val userCache = get<IUserCache>()
            val user = userCache.getUser()
            val state = authService.getState()
            if (state != null && user != null) {
                Navigation.Routes.HOME = Navigation.Routes.WELCOME
            } else {
                Navigation.Routes.HOME = Navigation.Routes.SIGN_IN
            }
        }

    }
}