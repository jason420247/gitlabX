package com.sozonov.gitlabx.utils.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sozonov.gitlabx.snackbar.Snackbar
import com.sozonov.gitlabx.snackbar.SnackbarData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.plus

abstract class BaseViewModel : ViewModel() {

    protected val viewModelScopeWithCEH =
        viewModelScope.plus(CoroutineExceptionHandler { _, throwable ->
            if (throwable !is Exception) {
                throw throwable
            }
            Log.e("CEH", throwable.message, throwable)
            Snackbar.show = SnackbarData(throwable.message ?: "Unknown error")
        })
}