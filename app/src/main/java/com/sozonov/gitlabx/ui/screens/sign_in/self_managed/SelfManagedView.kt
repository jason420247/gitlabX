package com.sozonov.gitlabx.ui.screens.sign_in.self_managed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SelfManagedView(viewModel: SelfManagedViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        var server by remember { viewModel.server }
        var accessToken by remember { viewModel.accessToken }
        var serverError by remember { mutableStateOf(false) }
        var accessTokenError by remember { mutableStateOf(false) }

        val checkServerValue = { value: String -> serverError = value.isBlank() }
        val checkAccessTokenValue = { value: String -> accessTokenError = value.isBlank() }


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
            keyboardActions = KeyboardActions(onDone = { viewModel.saveAuthState() })
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
            keyboardActions = KeyboardActions(onDone = { viewModel.saveAuthState() })
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = viewModel::saveAuthState) {
            Text("Save")
        }
    }
}