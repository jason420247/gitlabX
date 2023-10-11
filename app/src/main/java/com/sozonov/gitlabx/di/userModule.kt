package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.user.IUserCache
import com.sozonov.gitlabx.user.IUserRepository
import com.sozonov.gitlabx.user.IUserStateObserver
import com.sozonov.gitlabx.user.UserRealmImpl
import com.sozonov.gitlabx.user.UserRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

val userModule = module {
    singleOf(::UserRealmImpl) binds arrayOf(IUserCache::class, IUserStateObserver::class)
    single<IUserRepository> { UserRepositoryImpl(get(), get(), get()) }
}

