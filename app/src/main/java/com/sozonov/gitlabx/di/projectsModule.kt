package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.BuildConfig
import com.sozonov.gitlabx.di.debugstubs.ProjectRepositoryStub
import com.sozonov.gitlabx.projects.ProjectsViewModel
import com.sozonov.gitlabx.projects.repository.IProjectsRepository
import com.sozonov.gitlabx.projects.repository.ProjectsRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val projectsModule = module {
    if (!BuildConfig.DEBUG) {
        singleOf(::ProjectRepositoryStub) bind IProjectsRepository::class
    } else {
        singleOf(::ProjectsRepositoryImpl) bind IProjectsRepository::class
    }

    viewModelOf(::ProjectsViewModel)
}