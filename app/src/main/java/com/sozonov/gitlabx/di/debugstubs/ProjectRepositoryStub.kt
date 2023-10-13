package com.sozonov.gitlabx.di.debugstubs

import com.sozonov.gitlabx.projects.model.ProjectMetrics
import com.sozonov.gitlabx.projects.model.ProjectModel
import com.sozonov.gitlabx.projects.repository.IProjectsRepository
import com.sozonov.gitlabx.projects.repository.ProjectsOrder
import com.sozonov.gitlabx.utils.sort.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ProjectRepositoryStub : IProjectsRepository {
    override suspend fun getProjects(orderBy: ProjectsOrder, sort: Sort): List<ProjectModel> {
        return withContext(Dispatchers.IO) {
            delay(1000)
            listOf(
                ProjectModel(
                    1,
                    "descr",
                    "n1",
                    "sozonov / gitlabX",
                    Clock.System.now(),
                    Clock.System.now(),
                    Clock.System.now(),
                    true,
                    "develop",
                    "",
                    ProjectMetrics(1, 2, 3, 4),
                    true,
                    null
                ),
                ProjectModel(
                    2,
                    "descr",
                    "n2",
                    "sozonov / test",
                    Clock.System.now(),
                    Clock.System.now(),
                    Clock.System.now().minus(20.minutes),
                    false,
                    "master",
                    "",
                    ProjectMetrics(1, 2, 3, 4),
                    false,
                    null
                ),
                ProjectModel(
                    3,
                    "descr",
                    "n4",
                    "sozonov / test3",
                    Clock.System.now(),
                    Clock.System.now(),
                    Clock.System.now().minus(10.hours),
                    false,
                    "master",
                    "",
                    ProjectMetrics(1, 2, 3, 4),
                    false,
                    null
                ),
                ProjectModel(
                    4,
                    "descr",
                    "n4",
                    "sozonov / test 4",
                    Clock.System.now(),
                    Clock.System.now(),
                    Clock.System.now().minus(10.days),
                    false,
                    "master",
                    "",
                    ProjectMetrics(1, 2, 3, 4),
                    false,
                    null
                ),
                ProjectModel(
                    5,
                    "descr",
                    "n5",
                    "sozonov / test 5",
                    Clock.System.now(),
                    Clock.System.now(),
                    Clock.System.now().minus(700.days),
                    false,
                    "master",
                    "",
                    ProjectMetrics(1, 2, 3, 4),
                    false,
                    null
                ),
                ProjectModel(
                    6,
                    "descr",
                    "n6",
                    "sozonov / test 6",
                    Clock.System.now(),
                    Clock.System.now(),
                    Clock.System.now().minus(14.days),
                    false,
                    "master",
                    "",
                    ProjectMetrics(1, 2, 3, 4),
                    false,
                    null
                ),
                ProjectModel(
                    7,
                    "descr",
                    "n7",
                    "sozonov / test 7",
                    Clock.System.now(),
                    Clock.System.now(),
                    Clock.System.now().minus(66.days),
                    false,
                    "master",
                    "",
                    ProjectMetrics(1, 2, 3, 4),
                    false,
                    null
                )
            )
        }
    }
}