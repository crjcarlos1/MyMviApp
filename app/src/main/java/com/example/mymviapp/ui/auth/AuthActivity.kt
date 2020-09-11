package com.example.mymviapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.mymviapp.R
import com.example.mymviapp.ui.BaseActivity
import com.example.mymviapp.ui.auth.state.AuthStateEvent
import com.example.mymviapp.ui.main.MainActivity
import com.example.mymviapp.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
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
        checkAuthPreviousAuthUser()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    private fun subscriberObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            Log.d(TAG, "AuthActivity, dataState: $it")
                            viewModel.setAuthToken(it)
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

    private fun checkAuthPreviousAuthUser() {
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}









