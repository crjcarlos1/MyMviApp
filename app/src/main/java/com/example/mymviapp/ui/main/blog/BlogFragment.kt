package com.example.mymviapp.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymviapp.R
import com.example.mymviapp.models.BlogPost
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.main.blog.state.BlogViewState
import com.example.mymviapp.ui.main.blog.viewmodel.*
import com.example.mymviapp.util.ErrorHandling
import com.example.mymviapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class BlogFragment : BaseBlogFragment(), BlogListAdapter.Interaction {

    private lateinit var recyclerAdapter: BlogListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initRecyclerView()
        subscribeObservers()
        if (savedInstanceState == null) {
            viewModel.loadFirstPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //clean references (can leak memory)
        blog_post_recyclerview.adapter = null
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                handlePagination(dataState)
                stateChangeListener.onDataStateChange(dataState)
            }

        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            Log.d(TAG, "BlogFragment, ViewState: $viewState")
            if (viewState != null) {
                recyclerAdapter.submitList(
                    list = viewState.blogFields.blogList,
                    isQueryExhausted = viewState.blogFields.isQueryExhausted
                )
            }
        })
    }

    private fun handlePagination(dataState: DataState<BlogViewState>) {
        //handle incoming  data from  DataState
        /*
        dataState.data?.let {
            it.data?.let{
                it.getContentIfNotHandled()?.let{
                    viewModel.handleIncomingBlogListData(it)
                }
            }
        }
         */
        dataState.data?.let {
            it.data?.let {
                it.getContentIfNotHandled()?.let {
                    viewModel.handleIncomingBlogListData(it)
                }
            }
        }

        //Check for pagination end (ex: no more result)
        //must do this b/c server  will return ApiErrorResponse if page is not valid
        //   -> Meaning there is no more data
        dataState.error?.let { event ->
            event.peekContent().response.message?.let {
                if (ErrorHandling.isPaginationDone(it)) {

                    //handle the error message event so it does not display in UI
                    event.getContentIfNotHandled()

                    //set query exhausted to update RecyclerView with "No more results..." list item
                    viewModel.setQueryExhausted(true)
                }
            }
        }
    }

    private fun initRecyclerView() {
        blog_post_recyclerview.apply {

            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingItemDecoration = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingItemDecoration)
            addItemDecoration(topSpacingItemDecoration)
            recyclerAdapter = BlogListAdapter(
                requestManager = requestManager,
                interaction = this@BlogFragment
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "BlogFragment: attemting to load next page...")
                        viewModel.nextPage()
                    }
                }
            })

            adapter = recyclerAdapter

        }
    }

}