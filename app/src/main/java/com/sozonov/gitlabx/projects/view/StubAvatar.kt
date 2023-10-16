package com.sozonov.gitlabx.projects.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun StubAvatar(
    letter: Char,
    modifier: Modifier = Modifier
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