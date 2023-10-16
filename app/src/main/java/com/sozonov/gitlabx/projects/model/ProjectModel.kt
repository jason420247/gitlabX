package com.sozonov.gitlabx.projects.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Instant

@Immutable
data class ProjectModel(
    val id: Int,
    val description: String?,
    val name: String,
    val nameWithNamespace: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastActivityAt: Instant,
    val isPrivate: Boolean,
    val defaultBranch: String,
    val readmeUrl: String,
    val metrics: ProjectMetrics,
    val isMeCreator: Boolean,
    val avatarUrl: String?
)