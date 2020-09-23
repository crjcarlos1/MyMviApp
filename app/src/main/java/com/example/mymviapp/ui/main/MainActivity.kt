package com.example.mymviapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.bumptech.glide.RequestManager
import com.example.mymviapp.R
import com.example.mymviapp.models.AUTH_TOKEN_BUNDLE_KEY
import com.example.mymviapp.models.AuthToken
import com.example.mymviapp.ui.BaseActivity
import com.example.mymviapp.ui.auth.AuthActivity
import com.example.mymviapp.ui.main.account.BaseAccountFragment
import com.example.mymviapp.ui.main.account.ChangePasswordFragment
import com.example.mymviapp.ui.main.account.UpdateAccountFragment
import com.example.mymviapp.ui.main.blog.BaseBlogFragment
import com.example.mymviapp.ui.main.blog.UpdateBlogFragment
import com.example.mymviapp.ui.main.blog.ViewBlogFragment
import com.example.mymviapp.ui.main.create_blog.BaseCreateBlogFragment
import com.example.mymviapp.util.BottomNavController
import com.example.mymviapp.util.setUpNavigation
import com.example.mymviapp.viewmodels.ViewModelProviderFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class MainActivity : BaseActivity(), BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener, MainDependencyProvider {

    private lateinit var bottomNavigationView: BottomNavigationView

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @Inject
    lateinit var requestManager: RequestManager

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }

        subscribeObservers()
        restoreSession(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(AUTH_TOKEN_BUNDLE_KEY, sessionManager.cachedToken.value)
        super.onSaveInstanceState(outState)
    }

    private fun restoreSession(savedInstanceState: Bundle?) {
        savedInstanceState?.let { inState ->
            inState[AUTH_TOKEN_BUNDLE_KEY]?.let { authToken ->
                sessionManager.setValue(authToken as AuthToken)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    private fun setupActionBar() {
        setSupportActionBar(tool_bar)
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "MainActivity: subscribeObservers: AuthToken $authToken")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }
        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        else -> {
            R.navigation.nav_blog
        }
    }

    override fun onGraphChange() {
        expandAppBar()
        cancelActiveJobs()
    }

    private fun cancelActiveJobs() {
        val fragments =
            bottomNavController.fragmentManager
                .findFragmentById(bottomNavController.containerId)
                ?.childFragmentManager
                ?.fragments

        if (fragments != null) {
            for (fragment in fragments) {
                when (fragment) {
                    is BaseAccountFragment -> fragment.cancelActiveJobs()
                    is BaseBlogFragment -> fragment.cancelActiveJobs()
                    is BaseCreateBlogFragment -> fragment.cancelActiveJobs()
                }
            }
        }

        displayProgressBar(false)
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) =
        when (fragment) {
            is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
            }
            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
            }
            is UpdateAccountFragment -> {
                navController.navigate(R.id.action_updateAccountFragment_to_accountFragment)
            }
            is ChangePasswordFragment -> {
                navController.navigate(R.id.action_changePasswordFragment_to_accountFragment)
            }
            else -> {
                //do nothing
            }
        }

    override fun getVMProviderFactory() = providerFactory

    override fun getGlideRequestManager() = requestManager


}