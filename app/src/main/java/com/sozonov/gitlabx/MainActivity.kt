package com.sozonov.gitlabx

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.AuthService.Companion.AUTH_TAG
import com.sozonov.gitlabx.navigation.Destination
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.ui.screens.sign_in.SignInViewModel
import com.sozonov.gitlabx.ui.screens.sign_in.SingInView
import com.sozonov.gitlabx.ui.screens.sign_in.self_managed.SelfManagedView
import com.sozonov.gitlabx.ui.theme.GitlabXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mAuthService by inject<AuthService>()
    private val mAuthResultLauncher = registerForActivityAuthResult()
    private val signInViewModel by viewModel<SignInViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var gitlabCloudAuthProcessing by rememberSaveable {
                mutableStateOf(false)
            }
            val navController = rememberNavController()
            val lifecycle = LocalLifecycleOwner.current.lifecycle
            LaunchedEffect(lifecycle) {
                snapshotFlow { Navigation.destination }
                    .flowWithLifecycle(lifecycle)
                    .collect { dest ->
                        if (dest != null) {
                            if (dest.popUpRoute) {
                                navController.navigate(dest.route) {
                                    if (dest.launchSingleTop) {
                                        launchSingleTop = true
                                        return@navigate
                                    }
                                    popUpTo(dest.route) {
                                        inclusive = dest.inclusive
                                    }
                                    Log.i(Navigation.TAG, "navigating to $dest")
                                }
                            }
                            if (!dest.popUpRoute) {
                                navController.navigate(dest.route)
                                Log.i(Navigation.TAG, "navigating to $dest")
                            }
                            Navigation.destination = null
                        }
                    }
            }
            LaunchedEffect(lifecycle) {
                snapshotFlow { signInViewModel.userState }
                    .flowWithLifecycle(lifecycle)
                    .collect { user ->
                        if (user.id != null || user.errorMessage != null) {
                            gitlabCloudAuthProcessing = false;
                            if (user.errorMessage == null) {
                                checkNotNull(user.id)
                                Navigation.destination = Destination(Navigation.Routes.WELCOME, true)
                                return@collect
                            }
                            // show snackbar!! TODO
                        }
                    }
            }
            GitlabXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Navigation.Routes.SIGN_IN,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        composable(Navigation.Routes.SIGN_IN) {
                            fun doAuthorization() {
                                gitlabCloudAuthProcessing = true
                                mAuthResultLauncher.launch(mAuthService.provideAuthIntent())
                            }

                            fun doNavigationToSetupSelfManaged() {
                                lifecycleScope.launch {
                                    Navigation.destination =
                                        Destination(Navigation.Routes.SELF_MANAGED_SIGN_IN)
                                }
                            }
                            SingInView(
                                doOnGitlabCloud = ::doAuthorization,
                                doOnGitlabSelfManaged = ::doNavigationToSetupSelfManaged,
                                gitlabCloudAuthProcessing = gitlabCloudAuthProcessing
                            )
                        }
                        composable(Navigation.Routes.SELF_MANAGED_SIGN_IN) { SelfManagedView() }
                    }
                }
            }
        }
    }

    private fun registerForActivityAuthResult() =
        registerForActivityResult(StartActivityForResult()) { result ->
            when(result.resultCode){
                Activity.RESULT_CANCELED ->{
                    signInViewModel.produceUserError("Request cancelled")
                }
                Activity.RESULT_OK -> {
                    val data = result.data
                    data?.run {
                        val resp = AuthorizationResponse.fromIntent(data)
                        val exc = AuthorizationException.fromIntent(data)
                        lifecycleScope.launch(Dispatchers.IO) Code@{
                            mAuthService.store.handleResponse(resp, exc)
                            if (exc != null || resp == null) {
                                signInViewModel.produceUserError("Authorization error")
                                Log.e(AUTH_TAG, exc?.message ?: "error", exc)
                                return@Code
                            }
                            Log.i(AUTH_TAG, "auth response code saved")
                            mAuthService.performTokenRequest(
                                resp.createTokenExchangeRequest()
                            ) { responseToken, exc ->
                                lifecycleScope.launch(Dispatchers.IO) Token@{
                                    mAuthService.store.handleResponse(responseToken, exc)
                                    if (exc != null || responseToken == null) {
                                        signInViewModel.produceUserError("Authorization error")
                                        Log.e(AUTH_TAG, exc?.message ?: "error", exc)
                                        return@Token
                                    }
                                    Log.i(AUTH_TAG, "auth tokens saved")
                                    signInViewModel.fetchUser()
                                }
                            }
                        }
                    }
                }
                else -> {
                    Log.wtf(AUTH_TAG, "unknown code response")
                }
            }
        }
}