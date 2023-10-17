package com.sozonov.gitlabx.configuration

interface IApplicationConfiguration {
    val apiUrl: String

    companion object {
        val INSTANCE: IApplicationConfiguration = ApplicationConfigurationImpl()
    }
}