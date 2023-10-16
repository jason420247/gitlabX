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
import com.sozonov.gitlabx.utils.compose.spacer.HorizontalSpacer

@Composable
internal fun RepositoryDefaultBranch(defaultBranch: String) {
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