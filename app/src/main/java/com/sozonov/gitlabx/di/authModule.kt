package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.ui.sign_in.cloud.CloudSignInViewModel
import com.sozonov.gitlabx.auth.ui.sign_in.self_managed.SelfManagedSignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { SelfManagedSignInViewModel(get(), get()) }
    viewModel { CloudSignInViewModel(get()) }
}
internal val internalAuthModule = module {
    single { AuthService(get(), get()) }
}