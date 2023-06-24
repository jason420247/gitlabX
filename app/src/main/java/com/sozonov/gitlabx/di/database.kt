package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.user.dal.UserDao
import io.realm.kotlin.RealmConfiguration
import org.koin.dsl.module

val databaseModule = module {
    single {
        RealmConfiguration.create(schema = setOf(UserDao::class))
    }
}