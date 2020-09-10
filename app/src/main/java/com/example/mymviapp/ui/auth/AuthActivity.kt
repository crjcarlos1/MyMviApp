package com.example.mymviapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.mymviapp.ui.BaseActivity
import com.example.mymviapp.ui.ResponseType
import com.example.mymviapp.R
import com.example.mymviapp.ui.main.MainActivity
import com.example.mymviapp.viewmodels.ViewModelProviderFactory
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)
        subscriberObservers()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }

    private fun subscriberObservers() {

        viewModel.dataState.observe(this, Observer { dataState ->
            dataState.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            Log.d(TAG, "AuthActivity, dataState: $it")
                            viewModel.setAuthToken(it)
                        }
                    }
                }
                data.response?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        when (it.responseType) {
                            is ResponseType.Dialog -> {
                                //showDialog
                            }
                            is ResponseType.Toast -> {
                                //showToast
                            }
                            is ResponseType.None -> {
                                //print log
                                Log.e(
                                    TAG,
                                    "AuthActivity: Response ${it.message}, ${it.responseType}"
                                )
                            }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(this, Observer {
            Log.d(TAG, "AuthActivity, subscriberObservers AuthViewState: $it")
            it.authToken?.let {
                sessionManager.login(it)
            }
        })

        sessionManager.cachedToken.observe(this, Observer { dataState ->
            Log.d(TAG, "AuthActivity, subscriberObservers, AuthDataState $dataState")
            dataState.let { authToken ->
                if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                    navMainActivity()
                }
            }
        })

    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}









