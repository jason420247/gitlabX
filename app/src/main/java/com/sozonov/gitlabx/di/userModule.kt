package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.user.IUserCache
import com.sozonov.gitlabx.user.IUserRepository
import com.sozonov.gitlabx.user.UserCacheImpl
import com.sozonov.gitlabx.user.UserRepositoryImpl
import org.koin.dsl.module

val userModule = module {
    single<IUserCache> { UserCacheImpl() }
    single<IUserRepository> { UserRepositoryImpl(get(), get(), get()) }
}