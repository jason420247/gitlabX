package com.sozonov.gitlabx.di

import org.koin.dsl.module

val appModule = module {
    includes(authModule, httpClient, userModule, databaseModule)
}