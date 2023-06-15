package com.sozonov.gitlabx

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.AuthService.Companion.AUTH_TAG
import com.sozonov.gitlabx.navigation.Destination
import com.sozonov.gitlabx.navigation.IDestination.Companion.SelfManagedSignIn
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.navigation.PopUpTo
import com.sozonov.gitlabx.ui.screens.sign_in.SingInView
import com.sozonov.gitlabx.ui.screens.sign_in.self_managed.SelfManagedView
import com.sozonov.gitlabx.ui.theme.GitlabXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val mAuthService by inject<AuthService>()
    private val mAuthResultLauncher = registerForActivityAuthResult()
    private val mViewModel by viewModels<SignInViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val destinationsState = Navigation.destination.collectAsState(null, Dispatchers.Main)
            val destination by remember { destinationsState }
            if (destination != null) {
                when (destination) {
                    is Destination -> {
                        val dest = destination as Destination<*>
                        navController.navigate(dest.route)
                        Log.i(Navigation.TAG, "navigating to $dest")
                    }

                    is PopUpTo -> {
                        val popUp = destination as PopUpTo<*>
                        navController.navigate(popUp.route) {
                            if (popUp.launchSingleTop) {
                                launchSingleTop = true
                                return@navigate
                            }
                            popUpTo(requireNotNull(popUp.popUpRoute)) { inclusive = popUp.inclusive }
                            Log.i(Navigation.TAG, "navigating to $popUp")
                        }
                    }

                    else -> {
                        throw IllegalArgumentException(destination!!::class.simpleName)
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
                            SingInView(
                                doOnGitlabCloud = ::doAuthorization,
                                doOnGitlabSelfManaged = ::doNavigationToSetupSelfManaged,
                                gitlabCloudAuthProcessing = mViewModel.gitlabCloudAuthProcessing
                            )
                        }
                        composable(Navigation.Routes.SELF_MANAGED_SIGN_IN) { SelfManagedView() }
                    }
                }
            }
        }
    }

    private fun doAuthorization() {
        mViewModel.changeGitlabCloudAuthProcessing(true)
        mAuthResultLauncher.launch(mAuthService.provideAuthIntent())
    }

    private fun doNavigationToSetupSelfManaged() {
        lifecycleScope.launch {
            Navigation.route(SelfManagedSignIn)
        }
    }

    private fun registerForActivityAuthResult() = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            data?.run {
                val resp = AuthorizationResponse.fromIntent(data)
                val exc = AuthorizationException.fromIntent(data)
                lifecycleScope.launch(Dispatchers.IO) {
                    mAuthService.store.handleResponse(resp, exc)

                    if (exc != null || resp == null) {
                        Log.e(AUTH_TAG, exc?.message ?: "error", exc)
                        return@launch
                    }
                    Log.i(AUTH_TAG, "auth response code saved")
                    mAuthService.performTokenRequest(
                        resp.createTokenExchangeRequest()
                    ) { responseToken, exc ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            mAuthService.store.handleResponse(responseToken, exc)
                            withContext(Dispatchers.Main) {
                                mViewModel.changeGitlabCloudAuthProcessing(false)
                            }
                            if (exc != null || responseToken == null) {
                                Log.e(AUTH_TAG, exc?.message ?: "error", exc)
                                return@launch
                            }
                            Log.i(AUTH_TAG, "auth tokens saved")
                        }
                    }
                }
            }

        }
    }
}