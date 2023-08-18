package com.sozonov.gitlabx.snackbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

interface Snackbar {

    companion object {

        const val TAG = "SNACKBAR"

        var show by mutableStateOf<SnackbarData?>(null)
    }
}

