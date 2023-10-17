package com.sozonov.gitlabx.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.sozonov.gitlabx.R
import com.sozonov.gitlabx.navigation.Destination
import com.sozonov.gitlabx.navigation.Navigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun Welcome(viewModel: WelcomeViewModel = koinViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val userFullName by remember {
            derivedStateOf { viewModel.user.value?.fullName }
        }
        val userReady by remember {
            derivedStateOf { viewModel.user.value != null }
        }

        val lifecycle = LocalLifecycleOwner.current.lifecycle

        LaunchedEffect(userReady, lifecycle) {
            if (userReady) {
                launch {
                    delay(1500)
                    Navigation.destination =
                        Destination(
                            Navigation.Routes.PROJECTS,
                            popUpRoute = Navigation.Routes.WELCOME
                        )
                }
            }
        }

        AnimatedContent(targetState = userReady, transitionSpec = {
            fadeIn(
                animationSpec = tween(2000)
            ) togetherWith fadeOut(animationSpec = tween(2000))
        }, label = "Welcome view") { ready ->
            if (ready) {
                Text(
                    text = stringResource(
                        R.string.text_welcome,
                        userFullName!!
                    ),
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp
                )
            }
        }


    }
}