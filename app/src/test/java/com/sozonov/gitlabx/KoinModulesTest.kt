package com.sozonov.gitlabx

import android.app.Application
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.di.authModule
import com.sozonov.gitlabx.di.httpClient
import com.sozonov.gitlabx.di.userModule
import io.realm.kotlin.RealmConfiguration
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

class KoinModulesTest : KoinTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun verifyKoinApp() {
        koinApplication {
            modules(authModule, httpClient, userModule)
            checkModules {
                withInstance<Application>()
                withInstance<RealmConfiguration>()
                withInstance<AuthService>()
            }
        }
    }
}