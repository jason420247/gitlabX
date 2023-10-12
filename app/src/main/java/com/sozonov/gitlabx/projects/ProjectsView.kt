package com.sozonov.gitlabx.projects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProjectsView(projectsViewModel: ProjectsViewModel = koinViewModel()) {

    val projects by projectsViewModel.projects.collectAsState()

    Column {
        Text(text = "PROJECTS")
        LazyColumn {
            items(projects, key = { p -> p.id }, contentType = { "project" }) { pr ->
                Text(text = pr.name)
            }
        }
    }

}
