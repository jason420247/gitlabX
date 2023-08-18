package com.sozonov.gitlabx.ui.screens.sign_in.self_managed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import com.sozonov.gitlabx.navigation.Destination
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.snackbar.Snackbar
import com.sozonov.gitlabx.snackbar.SnackbarData
import org.koin.androidx.compose.koinViewModel

@Composable
fun SelfManagedView(
    viewModel: SelfManagedSignInViewModel = koinViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var fetchingUser by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var server by rememberSaveable { mutableStateOf("") }
        var accessToken by rememberSaveable { mutableStateOf("") }
        var serverError by rememberSaveable { mutableStateOf(false) }
        var accessTokenError by rememberSaveable { mutableStateOf(false) }

        val checkServerValue = { value: String -> serverError = value.isBlank() }
        val checkAccessTokenValue = { value: String -> accessTokenError = value.isBlank() }
        LaunchedEffect(lifecycle) {
            snapshotFlow { viewModel.userState }
                .flowWithLifecycle(lifecycle)
                .collect { user ->
                    if (user.id != null || user.errorMessage != null) {
                        if (user.errorMessage == null) {
                            checkNotNull(user.id)
                            fetchingUser = false
                            Navigation.destination = Destination(Navigation.Routes.WELCOME, true)
                            return@collect
                        }
                        fetchingUser = false
                        Snackbar.show = SnackbarData(user.errorMessage)
                    }
                }
        }
        TextField(
            value = server,
            onValueChange = {
                server = it
                checkServerValue(it)
            },
            label = { Text("Server") }, isError = serverError,
            modifier = Modifier.fillMaxWidth(1f),
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = when {
                    server.isNotBlank() && accessToken.isNotBlank() -> ImeAction.Done
                    else -> ImeAction.Next
                }
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.fetchUser(server, accessToken) })
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            value = accessToken,
            onValueChange = {
                accessToken = it
                checkAccessTokenValue(it)
            },
            label = { Text("Access Token") },
            isError = accessTokenError,
            modifier = Modifier.fillMaxWidth(1f),
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = when {
                    server.isNotBlank() && accessToken.isNotBlank() -> ImeAction.Done
                    else -> ImeAction.Previous
                }
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.fetchUser(server, accessToken) })
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = {
            fetchingUser = true
            viewModel.fetchUser(server, accessToken)
        }, enabled = !fetchingUser && (server.isNotBlank() && accessToken.isNotBlank())) {
            if (fetchingUser) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Save")
            }
        }
    }
}