package com.example.mymviapp.ui.main.blog

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
import com.example.mymviapp.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.example.mymviapp.ui.main.blog.state.BlogViewState
import com.example.mymviapp.ui.main.blog.viewmodel.BlogViewModel
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
abstract class BaseBlogFragment : Fragment(), Injectable {

    val TAG: String = "AppDebug"

    lateinit var dependencyProvider: MainDependencyProvider

    lateinit var viewModel: BlogViewModel

    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(
                this,
                dependencyProvider.getVMProviderFactory()
            ).get(BlogViewModel::class.java)
        } ?: throw  Exception("Invalid activity")
        cancelActiveJobs()

        //restore state  after process death
        savedInstanceState?.let { inState ->
            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (isViewModelInitialized()) {
            val viewState = viewModel.viewState.value

            viewState?.blogFields?.blogList = ArrayList()

            outState.putParcelable(BLOG_VIEW_STATE_BUNDLE_KEY, viewState)
        }
        super.onSaveInstanceState(outState)
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
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
        try {
            dependencyProvider = context as MainDependencyProvider
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement MainDependencyProvider")
        }
    }

    fun isViewModelInitialized() = ::viewModel.isInitialized

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