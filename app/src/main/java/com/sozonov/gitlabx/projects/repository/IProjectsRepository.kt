package com.sozonov.gitlabx.projects.repository

import com.sozonov.gitlabx.projects.model.ProjectModel

interface IProjectsRepository {
    suspend fun getProjects(): List<ProjectModel>
}