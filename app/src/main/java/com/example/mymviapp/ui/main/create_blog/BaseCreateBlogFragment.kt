package com.example.mymviapp.ui.main.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mymviapp.R
import com.example.mymviapp.di.Injectable
import com.example.mymviapp.ui.DataStateChangeListener
import com.example.mymviapp.ui.UICommunicationListener
import com.example.mymviapp.ui.main.MainDependencyProvider
import com.example.mymviapp.ui.main.account.AccountViewModel
import com.example.mymviapp.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.example.mymviapp.ui.main.account.state.AccountViewState
import com.example.mymviapp.ui.main.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.example.mymviapp.ui.main.create_blog.state.CreateBlogViewState
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
abstract class BaseCreateBlogFragment : Fragment(), Injectable {

    val TAG: String = "AppDebug"

    lateinit var dependencyProvider: MainDependencyProvider

    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var uiCommunicationListener: UICommunicationListener

    lateinit var viewModel: CreateBlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this, dependencyProvider.getVMProviderFactory()).get(
                CreateBlogViewModel::class.java
            )
        } ?: throw Exception("Invalid Activity")
        cancelActiveJobs()

        //restore state  after process death
        savedInstanceState?.let { inState ->
            (inState[CREATE_BLOG_VIEW_STATE_BUNDLE_KEY] as CreateBlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (isViewModelInitialized()) {
            outState.putParcelable(CREATE_BLOG_VIEW_STATE_BUNDLE_KEY, viewModel.viewState.value)
        }
        super.onSaveInstanceState(outState)
    }

    fun isViewModelInitialized() = ::viewModel.isInitialized

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: java.lang.ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
        try {
            dependencyProvider = context as MainDependencyProvider
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement MainDependencyProvider")
        }
    }


    /*
     @fragmentId is id of fragment from graph to be EXCLUDED from action back bar nav
    */
    fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

    fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

}