package com.sozonov.gitlabx.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sozonov.gitlabx.R
import com.sozonov.gitlabx.projects.model.ProjectMetrics
import com.sozonov.gitlabx.projects.model.ProjectModel
import com.sozonov.gitlabx.utils.compose.spacer.HorizontalSpacer
import com.sozonov.gitlabx.utils.compose.spacer.VerticalSpacer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProjectsView(projectsViewModel: ProjectsViewModel = koinViewModel()) {

    val projects by projectsViewModel.projects.collectAsState()

    Column {
        LazyColumn {
            items(projects, key = { p -> p.id }) { pr ->
                ProjectItem(pr)
            }
        }
    }
}

@Composable
private fun ProjectItem(project: ProjectModel) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (project.avatarUrl == null) {
            StubAvatar(project.name.first())
        } else {
            StubAvatar('?')
        }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Text(text = project.nameWithNamespace, style = MaterialTheme.typography.titleSmall)
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

@Composable
@Preview
private fun ProjectsMetrics(metrics: ProjectMetrics = ProjectMetrics(0, 0, 0, 0)) {
    val metric = @Composable { res: Int, desc: Int, count: Int ->
        AsyncImage(
            model = res,
            contentDescription = stringResource(desc),
            modifier = Modifier.size(12.dp)
        )
        HorizontalSpacer(space = 2)
        Text(text = count.toString(), style = MaterialTheme.typography.labelSmall)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        val space = 8
        metric(R.drawable.stars_gitlab, R.string.stars, metrics.starsCount)
        HorizontalSpacer(space = space)
        metric(R.drawable.fork_gitlab, R.string.forks, metrics.forksCount)
        HorizontalSpacer(space = space)
        metric(R.drawable.merge_gitlab, R.string.merge_requests, metrics.mergeRequestsCount)
        HorizontalSpacer(space = space)
        metric(R.drawable.issues_gitlab, R.string.issues, metrics.issuesCount)
    }
}

@Composable
private fun ProjectDates(lastActivity: Instant) {
    val diff = Clock.System.now().minus(lastActivity)
    //   val isRecently = diff.inWholeSeconds <= 59

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        val text = @Composable { str: String ->
            Text(
                text = str,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.typography.labelSmall.color.copy(
                        alpha = 0.5f
                    )
                )
            )
        }
        AsyncImage(
            model = R.drawable.update_gitlab,
            contentDescription = stringResource(R.string.project_updated),
            modifier = Modifier.size(12.dp)
        )
        HorizontalSpacer(space = 2)
        var textValue = ""
        val minutes = diff.inWholeMinutes
        val hours = diff.inWholeHours
        val days = diff.inWholeDays
        val weeks = days / 7
        val months = weeks / 4
        val years = months / 12
        textValue = when {
            diff.inWholeSeconds <= 59 -> stringResource(R.string.recently)
            minutes < 60 -> stringResource(R.string.minutes_ago, minutes)
            hours < 24 -> stringResource(R.string.hours_ago, hours)
            days < 7 -> stringResource(R.string.days_ago, days)
            weeks < 4 -> stringResource(R.string.weeks_ago, weeks)
            months < 12 -> stringResource(R.string.months_ago, months)
            years == 1L -> stringResource(R.string.year_ago)
            else -> stringResource(R.string.more_than_1_year)
        }
        text(textValue)
    }
}

@Composable
private fun MeCreator() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = "Owner",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.padding(6.dp, 3.dp),
        )
    }

}

@Composable
private fun RepositoryDefaultBranch(defaultBranch: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = R.drawable.default_branch_gitlab,
            contentDescription = stringResource(R.string.default_branch),
            modifier = Modifier.size(12.dp)
        )
        HorizontalSpacer(4)
        Text(text = defaultBranch, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun RepositoryVisibilityImage(
    isPrivate: Boolean
) {
    val img = @Composable { drawable: Int, description: Int ->
        AsyncImage(
            model = drawable,
            contentDescription = stringResource(description),
            modifier = Modifier.size(16.dp)
        )
    }
    if (isPrivate) {
        img(R.drawable.private_gitlab, R.string.private_repo)
    } else {
        img(R.drawable.public_gitlab, R.string.public_repo)
    }
}

@Composable
private fun StubAvatar(
    letter: Char,
    modifier: Modifier = Modifier
        .padding(16.dp)
        .size(56.dp)
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface.copy(alpha = 1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter.uppercaseChar().toString(),

                style = MaterialTheme.typography.headlineLarge
            )
        }
    }

}
