package com.sozonov.gitlabx.ui.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sozonov.gitlabx.R
import com.sozonov.gitlabx.navigation.Destination
import com.sozonov.gitlabx.navigation.Navigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeView(userFullName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val lifecycle = LocalLifecycleOwner.current.lifecycle

        Text(
            text = stringResource(R.string.text_welcome, userFullName),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        CircularProgressIndicator()

        LaunchedEffect(lifecycle) {
            launch {
                delay(1000)
                Navigation.destination =
                    Destination(Navigation.Routes.PROJECTS, popUpRoute = Navigation.Routes.SIGN_IN)
            }
        }
    }
}