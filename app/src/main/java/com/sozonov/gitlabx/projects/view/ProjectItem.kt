package com.sozonov.gitlabx.projects.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sozonov.gitlabx.projects.model.ProjectModel
import com.sozonov.gitlabx.utils.compose.spacer.HorizontalSpacer
import com.sozonov.gitlabx.utils.compose.spacer.VerticalSpacer

@Composable
internal fun ProjectItem(project: ProjectModel) {
    Box(modifier = Modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (project.avatarUrl == null) {
                StubAvatar(project.name.first())
            } else {
                StubAvatar('?')
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Text(
                        text = project.nameWithNamespace,
                        style = MaterialTheme.typography.titleSmall
                    )
                    HorizontalSpacer(space = 4)
                    RepositoryVisibilityImage(project.isPrivate)

                    if (project.isMeCreator) {
                        HorizontalSpacer(space = 4)
                        MeCreator()
                    }
                }
                VerticalSpacer(space = 4)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RepositoryDefaultBranch(project.defaultBranch)

                    ProjectsMetrics(project.metrics)
                }
                VerticalSpacer(space = 4)
                ProjectDates(project.lastActivityAt)
            }
        }
    }
}