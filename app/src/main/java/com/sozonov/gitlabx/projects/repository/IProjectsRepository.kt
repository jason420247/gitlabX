package com.sozonov.gitlabx.projects.repository

import com.sozonov.gitlabx.projects.model.ProjectModel
import com.sozonov.gitlabx.utils.sort.Sort

interface IProjectsRepository {
    suspend fun getProjects(
        orderBy: ProjectsOrder = ProjectsOrder.NAME,
        sort: Sort = Sort.ASC
    ): List<ProjectModel>
}

enum class ProjectsOrder(val value: String) {
    ID("id"),
    NAME("NAME"),
    PATH("PATH"),
    CREATED_AT("CREATED_AT"),
    UPDATED_AT("UPDATED_AT"),
    LAST_ACTIVITY_AT("LAST_ACTIVITY_AT"),
    SIMILARITY("SIMILARITY")
}