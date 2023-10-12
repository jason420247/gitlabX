package com.sozonov.gitlabx.projects.model

import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

@Immutable
data class ProjectModel(
    val id: Int,
    val description: String?,
    val name: String,
    val nameWithNamespace: String,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val lastActivityAt: ZonedDateTime,
    val visibility: String,
    val defaultBranch: String,
    val readmeUrl: String,
    val metrics: ProjectMetrics,
    val isMeCreator: Boolean,
    val avatarUrl: String?
)