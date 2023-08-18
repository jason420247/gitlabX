package com.sozonov.gitlabx.ui.screens.sign_in.cloud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sozonov.gitlabx.ui.theme.Typography
import com.sozonov.gitlabx.utils.delegates.Action

@Composable
fun SingInView(
    doOnGitlabCloud: Action,
    doOnGitlabSelfManaged: Action,
    gitlabCloudAuthProcessing: Boolean
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Text("Welcome to GitlabX", style = Typography.titleLarge)

        Spacer(Modifier.height(24.dp))

        Text("Sign in", style = Typography.displayMedium)

        Spacer(Modifier.height(24.dp))

        Column {
            ElevatedButton(
                onClick = doOnGitlabCloud,
                modifier = Modifier.fillMaxWidth(0.7f),
                enabled = !gitlabCloudAuthProcessing
            ) {
                Text("Gitlab Cloud", style = Typography.bodyLarge)
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.width(16.dp)) {
                    if (gitlabCloudAuthProcessing)
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                }
            }

            Spacer(Modifier.height(16.dp))

            ElevatedButton(
                onClick = doOnGitlabSelfManaged,
                modifier = Modifier.fillMaxWidth(0.7f),
                enabled = !gitlabCloudAuthProcessing
            ) {
                Text("Gitlab self-managed", style = Typography.bodyLarge)
            }
        }
    }
}