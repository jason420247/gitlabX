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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.AuthService.Companion.AUTH_TAG
import com.sozonov.gitlabx.auth.ui.sign_in.cloud.CloudSignInViewModel
import com.sozonov.gitlabx.auth.ui.sign_in.cloud.SingInView
import com.sozonov.gitlabx.auth.ui.sign_in.self_managed.SelfManagedView
import com.sozonov.gitlabx.navigation.Destination
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.projects.ProjectsView
import com.sozonov.gitlabx.snackbar.Snackbar
import com.sozonov.gitlabx.snackbar.SnackbarData
import com.sozonov.gitlabx.theme.GitlabXTheme
import com.sozonov.gitlabx.welcome.WelcomeView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mAuthService by inject<AuthService>()
    private val mAuthResultLauncher = registerForActivityAuthResult()
    private val cloudSignInViewModel by viewModel<CloudSignInViewModel>()
    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createView()
        collectUserCreatedState()
    }

    private fun registerForActivityAuthResult() =
        registerForActivityResult(StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_CANCELED -> {
                    cloudSignInViewModel.produceUserError(getString(R.string.error_request_cancelled))
                }

                Activity.RESULT_OK -> {
                    val data = result.data
                    data?.run {
                        val resp = AuthorizationResponse.fromIntent(data)
                        val exc = AuthorizationException.fromIntent(data)
                        lifecycleScope.launch(Dispatchers.IO) Code@{
                            mAuthService.store.handleResponse(resp, exc)
                            if (exc != null || resp == null) {
                                cloudSignInViewModel.produceUserError(getString(R.string.error_authorization_error))
                                Log.e(
                                    AUTH_TAG,
                                    exc?.message ?: getString(R.string.error_error),
                                    exc
                                )
                                return@Code
                            }
                            Log.i(AUTH_TAG, getString(R.string.auth_response_code_saved))
                            mAuthService.performTokenRequest(
                                resp.createTokenExchangeRequest()
                            ) { responseToken, exc ->
                                lifecycleScope.launch(Dispatchers.IO) Token@{
                                    mAuthService.store.handleResponse(responseToken, exc)
                                    if (exc != null || responseToken == null) {
                                        cloudSignInViewModel.produceUserError(getString(R.string.error_authorization_error))
                                        Log.e(
                                            AUTH_TAG,
                                            exc?.message ?: getString(R.string.error_error),
                                            exc
                                        )
                                        return@Token
                                    }
                                    Log.i(AUTH_TAG, getString(R.string.auth_tokens_saved))
                                    cloudSignInViewModel.fetchUser()
                                }
                            }
                        }
                    }
                }

                else -> {
                    Log.wtf(AUTH_TAG, getString(R.string.error_unknown_code_response))
                }
            }
        }

    private fun createView() {
        setContent {
            KoinAndroidContext {
                var gitlabCloudAuthProcessing by rememberSaveable {
                    mutableStateOf(false)
                }
                val navController = rememberNavController()
                val lifecycle = LocalLifecycleOwner.current.lifecycle

                lifecycle.apply {
                    CollectNavDestination(navController)
                    CollectUserState { gitlabCloudAuthProcessing = it }
                }

                GitlabXTheme {
                    val snackbarHostState = remember { SnackbarHostState() }
                    lifecycle.CollectSnackbar(snackbarHostState)

                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        }
                    ) { padding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Navigation.Routes.SIGN_IN
                            ) {
                                composable(Navigation.Routes.SIGN_IN) {

                                    SingInView(
                                        doOnGitlabCloud = {
                                            gitlabCloudAuthProcessing = true
                                            mAuthResultLauncher.launch(mAuthService.provideAuthIntent())
                                        },
                                        doOnGitlabSelfManaged = {
                                            launch {
                                                Navigation.destination =
                                                    Destination(Navigation.Routes.SELF_MANAGED_SIGN_IN)
                                            }
                                        },
                                        gitlabCloudAuthProcessing = gitlabCloudAuthProcessing
                                    )
                                }
                                composable(Navigation.Routes.SELF_MANAGED_SIGN_IN) { SelfManagedView() }
                                composable(
                                    Navigation.Routes.WELCOME + "{${Navigation.Routes.Args.WELCOME_FULL_NAME}}",
                                    arguments = listOf(navArgument(Navigation.Routes.Args.WELCOME_FULL_NAME) {
                                        type = NavType.StringType
                                    })
                                ) { backStackEntry ->
                                    val user =
                                        backStackEntry.arguments?.getString(Navigation.Routes.Args.WELCOME_FULL_NAME)
                                            ?: throw IllegalArgumentException(getString(R.string.error_welcome_screen_should_contains_full_name_of_the_user))
                                    WelcomeView(user)
                                }
                                composable(Navigation.Routes.PROJECTS) {
                                    ProjectsView()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun collectUserCreatedState() {
        lifecycleScope.launch {
            mainViewModel.userCreated.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect { user ->
                    Navigation.destination =
                        Destination(
                            Navigation.Routes.WELCOME + user.fullName,
                            Navigation.Routes.WELCOME
                        )
                }
        }
    }

    private fun collectUserDeletedState() {
        lifecycleScope.launch {
            mainViewModel.userDeleted.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect { user ->
                    // todo
                }
        }
    }

    @Composable
    private fun Lifecycle.CollectSnackbar(snackbarHostState: SnackbarHostState) {
        LaunchedEffect(this) {
            snapshotFlow { Snackbar.show }.flowWithLifecycle(lifecycle)
                .distinctUntilChanged().collect { data ->
                    if (data != null) {
                        snackbarHostState.showSnackbar(
                            message = data.message,
                            duration = data.duration
                        )
                    }
                }
        }
    }

    @Composable
    private fun Lifecycle.CollectUserState(onProcessingChange: (flag: Boolean) -> Unit) {
        LaunchedEffect(this) {
            snapshotFlow { cloudSignInViewModel.userState }
                .flowWithLifecycle(lifecycle)
                .collect { user ->
                    if (user.id != null || user.errorMessage != null) {
                        onProcessingChange(false)
                        if (user.errorMessage != null) {
                            Snackbar.show = SnackbarData(user.errorMessage)
                        }

                    }
                }
        }
    }

    @Composable
    private fun Lifecycle.CollectNavDestination(navController: NavHostController) {
        LaunchedEffect(this) {
            snapshotFlow { Navigation.destination }
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { dest ->
                    if (dest != null) {
                        navController.navigate(dest.route) {
                            restoreState = true
                            launchSingleTop = dest.launchSingleTop
                            if (dest.popUpRoute != null) {
                                popUpTo(dest.popUpRoute) {
                                    inclusive = dest.inclusive
                                    saveState = true
                                }
                            }
                        }
                    }
                }
        }
    }
}