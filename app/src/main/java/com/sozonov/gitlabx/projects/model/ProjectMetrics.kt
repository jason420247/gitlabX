package com.sozonov.gitlabx.projects.model

import androidx.compose.runtime.Immutable

@Immutable
data class ProjectMetrics(
    val starsCount: Int,
    val forksCount: Int,
    val issuesCount: Int,
    val mergeRequestsCount: Int
)