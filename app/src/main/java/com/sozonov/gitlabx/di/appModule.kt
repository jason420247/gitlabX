package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.ui.screens.sign_in.self_managed.SelfManagedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AuthService(get()) }
    viewModel { SelfManagedViewModel(get()) }
}