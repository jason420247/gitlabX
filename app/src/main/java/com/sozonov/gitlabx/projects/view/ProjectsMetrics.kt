package com.sozonov.gitlabx.projects.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sozonov.gitlabx.R
import com.sozonov.gitlabx.projects.model.ProjectMetrics
import com.sozonov.gitlabx.utils.compose.spacer.HorizontalSpacer

@Composable
internal fun ProjectsMetrics(metrics: ProjectMetrics = ProjectMetrics(0, 0, 0, 0)) {
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