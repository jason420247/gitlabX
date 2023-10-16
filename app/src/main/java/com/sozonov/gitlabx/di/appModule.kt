package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.welcome.WelcomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::WelcomeViewModel)
}