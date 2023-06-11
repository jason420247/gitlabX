package com.sozonov.gitlabx

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.AuthStateStore
import com.sozonov.gitlabx.ui.screens.sign_in.SingInView
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
            GitlabXTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SingInView(
                        doOnGitlabCloud = ::doAuthorization,
                        doOnGitlabSelfManaged = ::doNavigationToSetupSelfManaged
                    )
                }
            }
        }
    }

    private fun doAuthorization() {
        mViewModel.changeGitlabCloudAuthProcessing(true)
        mAuthResultLauncher.launch(mAuthService.provideAuthIntent())
    }

    private fun doNavigationToSetupSelfManaged() {}

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