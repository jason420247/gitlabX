package com.sozonov.gitlabx.projects.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sozonov.gitlabx.projects.ProjectsViewModel
import com.sozonov.gitlabx.utils.compose.pullrefresh.PullRefreshIndicator
import com.sozonov.gitlabx.utils.compose.pullrefresh.pullRefresh
import com.sozonov.gitlabx.utils.compose.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun Projects(projectsViewModel: ProjectsViewModel = koinViewModel()) {

    val projects by projectsViewModel.projects.collectAsState()
    val refreshScope = rememberCoroutineScope()
    val pullState =
        rememberPullRefreshState(refreshing = projectsViewModel.loadingState.value, onRefresh = {
            refreshScope.launch {
                projectsViewModel.updateProjects()
            }
        })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .pullRefresh(pullState)
    ) {
        LazyColumn {
            items(projects, key = { p -> p.id }, contentType = { "project" }) { pr ->
                ProjectItem(pr)
            }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(alignment = Alignment.TopCenter),
            refreshing = projectsViewModel.loadingState.value,
            state = pullState,
        )
    }
}

