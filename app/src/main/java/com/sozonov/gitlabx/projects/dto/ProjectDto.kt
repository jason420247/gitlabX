package com.sozonov.gitlabx.projects.dto

import kotlinx.serialization.Serializable

@Serializable
internal class ProjectDto(
    val id: Int,
    val description: String?,
    val name: String,
    val name_with_namespace: String,
    val created_at: String,
    val updated_at: String,
    val last_activity_at: String,
    val visibility: String,
    val default_branch: String,
    val readme_url: String,
    val star_count: Int,
    val open_issues_count: Int,
    val forks_count: Int,
    val _links: ProjectLinks,
    val creator_id: Int,
    val avatar_url: String?
)

