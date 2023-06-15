package com.sozonov.gitlabx.ui.screens.sign_in

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sozonov.gitlabx.ui.theme.Typography
import com.sozonov.gitlabx.utils.delegates.Action

@Composable
fun SingInView(
    doOnGitlabCloud: Action,
    doOnGitlabSelfManaged: Action,
    gitlabCloudAuthProcessing: State<Boolean>
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
            val gitlabCloudProcessing by remember { gitlabCloudAuthProcessing }
            ElevatedButton(
                onClick = doOnGitlabCloud,
                modifier = Modifier.fillMaxWidth(0.7f),
                enabled = !gitlabCloudProcessing
            ) {
                Text("Gitlab Cloud", style = Typography.bodyLarge)
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.width(16.dp)) {
                    if (gitlabCloudProcessing)
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }

            Spacer(Modifier.height(16.dp))

            ElevatedButton(
                onClick = doOnGitlabSelfManaged,
                modifier = Modifier.fillMaxWidth(0.7f),
                enabled = !gitlabCloudProcessing
            ) {
                Text("Gitlab self-managed", style = Typography.bodyLarge)
            }
        }
    }
}