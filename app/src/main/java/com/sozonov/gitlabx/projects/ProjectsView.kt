package com.sozonov.gitlabx.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sozonov.gitlabx.projects.model.ProjectModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProjectsView(projectsViewModel: ProjectsViewModel = koinViewModel()) {

    val projects by projectsViewModel.projects.collectAsState()

    Column {
        LazyColumn {
            items(projects, key = { p -> p.id }, contentType = { "project" }) { pr ->
                ProjectItem(pr)
            }
        }
    }

}

@Composable
private fun ProjectItem(project: ProjectModel) {
    Row(Modifier.padding(4.dp)) {
        if (project.avatarUrl == null) {
            StubAvatar(project.name.first())
        } else {
            StubAvatar('?')
        }
        Column {
            Row {
                Text(text = project.nameWithNamespace)
                Text(text = project.visibility)
                Text(text = project.defaultBranch)
                if (project.isMeCreator) {
                    Text(text = "Owner")
                }
            }
            Row {
                Row {
                    /* Text(text = project.createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/hh/mm")))
                     Text(text = project.updatedAt.format(DateTimeFormatter.ofPattern("yyyy/MM/hh/mm")))
                     Text(text = project.lastActivityAt.format(DateTimeFormatter.ofPattern("yyyy/MM/hh/m")))
                 */
                }
                Row {
                    /*Text(text = project.metrics.mergeRequestsCount.toString())
                    Text(text = project.metrics.starsCount.toString())
                    Text(text = project.metrics.forksCount.toString())
                    Text(text = project.metrics.issuesCount.toString())*/
                }
            }
        }
    }
}

@Composable
private fun StubAvatar(
    letter: Char,
    modifier: Modifier = Modifier
        .padding(16.dp)
        .size(56.dp),
    fontSize: TextUnit = 48.sp,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface.copy(alpha = 1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter.uppercaseChar().toString(),
                fontSize = fontSize,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

}
