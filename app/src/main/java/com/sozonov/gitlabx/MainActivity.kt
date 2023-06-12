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
import com.sozonov.gitlabx.auth.AuthStateStore
import com.sozonov.gitlabx.navigation.Destination
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.ui.screens.sign_in.SingInView
import com.sozonov.gitlabx.ui.screens.sign_in.self_managed.SelfManagedView
import com.sozonov.gitlabx.ui.theme.GitlabXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class MainActivity : ComponentActivity() {
    private val mAuthService by lazy { AuthService(this) }
    private val mViewModel by viewModels<SignInViewModel>()
    private val mAuthResultLauncher = registerForActivityAuthResult()
    private val mStore by lazy { AuthStateStore.getInstance(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val destinationsState = Navigation.next.collectAsState(null, Dispatchers.Main)
            val popUpState = Navigation.popUpTo.collectAsState(null, Dispatchers.Main)
            val destination by remember { destinationsState }
            val popUp by remember { popUpState }
            if (destination != null) {
                Navigation.next(null)
                Log.i(Navigation.TAG, "navigating to ${destination!!}")
                navController.navigate(destination!!.route)
            }
            if (popUp != null) {
                Log.i(Navigation.TAG, "navigating to ${popUp!!}")
                navController.navigate(popUp!!.route) {
                    if (popUp!!.launchSingleTop) {
                        launchSingleTop = popUp!!.launchSingleTop
                        Navigation.popUpTo(null)
                        return@navigate
                    }
                    Navigation.popUpTo(null)
                    popUpTo(requireNotNull(popUp!!.popUpRoute)) { inclusive = popUp!!.inclusive }
                }
            }
            GitlabXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = Navigation.Routes.SIGN_IN) {
                        composable(Navigation.Routes.SIGN_IN) {
                            SingInView(
                                doOnGitlabCloud = ::doAuthorization,
                                doOnGitlabSelfManaged = ::doNavigationToSetupSelfManaged
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
            Navigation.next(Destination<Unit>(Navigation.Routes.SELF_MANAGED_SIGN_IN))
        }
    }

    private fun registerForActivityAuthResult() = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            data?.run {
                val resp = AuthorizationResponse.fromIntent(data)
                val exc = AuthorizationException.fromIntent(data)
                lifecycleScope.launch(Dispatchers.IO) {
                    mStore.handleResponse(resp, exc)

                    if (exc != null || resp == null) {
                        Log.e("auth", exc?.message ?: "error", exc)
                        return@launch
                    }
                    Log.i("auth", "auth response code saved")
                    mAuthService.performTokenRequest(
                        resp.createTokenExchangeRequest()
                    ) { responseToken, exc ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            mStore.handleResponse(responseToken, exc)
                            withContext(Dispatchers.Main) {
                                mViewModel.changeGitlabCloudAuthProcessing(false)
                            }
                            if (exc != null || responseToken == null) {
                                Log.e("auth", exc?.message ?: "error", exc)
                                return@launch
                            }
                            Log.i("auth", "auth tokens saved")
                        }
                    }
                }
            }

        }
    }
}