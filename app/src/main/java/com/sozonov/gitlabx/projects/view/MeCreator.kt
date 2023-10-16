package com.sozonov.gitlabx.projects.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sozonov.gitlabx.R

@Composable
internal fun MeCreator() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = stringResource(R.string.owner),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.padding(6.dp, 3.dp),
        )
    }

}