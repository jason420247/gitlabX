package com.sozonov.gitlabx.projects.repository

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.common.BaseRepository
import com.sozonov.gitlabx.projects.dto.ProjectDto
import com.sozonov.gitlabx.projects.dto.ProjectMergeRequestDto
import com.sozonov.gitlabx.projects.model.ProjectMetrics
import com.sozonov.gitlabx.projects.model.ProjectModel
import com.sozonov.gitlabx.user.repository.IUserRepository
import com.sozonov.gitlabx.utils.sort.Sort
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

private const val PRIVATE = "private"

internal class ProjectsRepositoryImpl(
    private val userRepository: IUserRepository,
    private val httpClient: HttpClient,
    authService: AuthService
) : IProjectsRepository, BaseRepository(httpClient, authService) {
    override suspend fun getProjects(order: ProjectsOrder, sort: Sort): List<ProjectModel> =
        withContext(Dispatchers.IO) {
            val currentUserId = userRepository.getUser().id
            val projectsDtos = provideApiClient().get("projects") {
                parameter("membership", true)
                parameter("orderBy", order.value)
                parameter("sort", sort.value)
            }.body<Array<ProjectDto>>()

            projectsDtos.map { pr ->
                async {
                    val mrCount = httpClient.get(Url(pr._links.merge_requests))
                        .body<Array<ProjectMergeRequestDto>>().size
                    ProjectModel(
                        id = pr.id,
                        description = pr.description,
                        name = pr.name,
                        nameWithNamespace = pr.name_with_namespace,
                        createdAt = Instant.parse(pr.created_at),
                        updatedAt = Instant.parse(pr.updated_at),
                        lastActivityAt = Instant.parse(pr.last_activity_at),
                        defaultBranch = pr.default_branch,
                        metrics = ProjectMetrics(
                            pr.star_count,
                            pr.forks_count,
                            pr.open_issues_count,
                            mrCount
                        ),
                        isPrivate = pr.visibility == PRIVATE,
                        readmeUrl = pr.readme_url,
                        isMeCreator = pr.creator_id == currentUserId,
                        avatarUrl = pr.avatar_url
                    )
                }

            }.awaitAll()
        }
}