package com.sozonov.gitlabx.projects.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.sozonov.gitlabx.utils.compose.spacer.HorizontalSpacer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
internal fun ProjectDates(lastActivity: Instant) {
    val diff = Clock.System.now().minus(lastActivity)

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