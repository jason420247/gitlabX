package com.sozonov.gitlabx.utils.httpClient

import com.sozonov.gitlabx.configuration.IApplicationConfiguration

class HttpClientWithBasePathBuilder private constructor() {

    companion object {
        private const val apiPath = "api/"
        private const val versionPath = "v4/"

        @JvmStatic
        fun buildApiPath(): String {
            val configuration = IApplicationConfiguration.INSTANCE
            val baseUrl = StringBuilder()
            baseUrl.append(configuration.apiUrl)
            if (configuration.apiUrl.last() != '/') {
                baseUrl.append("/")
            }
            baseUrl.append(apiPath).append(versionPath)
            return baseUrl.toString()
        }
    }
}