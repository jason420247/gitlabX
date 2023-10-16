package com.sozonov.gitlabx.projects.view

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sozonov.gitlabx.R

@Composable
internal fun RepositoryVisibilityImage(
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