package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.user.dal.IUserCache
import com.sozonov.gitlabx.user.dal.UserRealmImpl
import com.sozonov.gitlabx.user.repository.IUserRepository
import com.sozonov.gitlabx.user.repository.UserRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

val userModule = module {
    singleOf(::UserRealmImpl) binds arrayOf(IUserCache::class)
    single<IUserRepository> { UserRepositoryImpl(get(), get(), get()) }
}

