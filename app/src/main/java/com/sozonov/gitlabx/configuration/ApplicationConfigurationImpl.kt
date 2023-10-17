package com.sozonov.gitlabx.configuration

internal class ApplicationConfigurationImpl internal constructor() : IApplicationConfiguration {

    override val apiUrl: String
        get() = selfManagedUrl ?: GITLAB_CLOUD_URL

    companion object {
        private const val GITLAB_CLOUD_URL = "https://gitlab.com/"
        internal var selfManagedUrl: String? = null
    }
}