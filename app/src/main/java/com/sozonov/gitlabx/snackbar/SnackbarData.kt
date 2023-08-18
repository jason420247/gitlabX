package com.sozonov.gitlabx.snackbar

import androidx.compose.material3.SnackbarDuration

data class SnackbarData(
    val message: String,
    val duration: SnackbarDuration = SnackbarDuration.Short
)