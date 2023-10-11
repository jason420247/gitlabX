package com.sozonov.gitlabx.utils.delegates

import kotlinx.coroutines.CoroutineScope

typealias Action = () -> Unit
typealias CoroutineAction = CoroutineScope.() -> Unit
