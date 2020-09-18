package com.example.mymviapp.ui.main.blog


import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mymviapp.R
import com.example.mymviapp.models.BlogPost
import com.example.mymviapp.ui.main.blog.state.BlogStateEvent
import com.example.mymviapp.ui.main.blog.viewmodel.isAuthorOfBlogPost
import com.example.mymviapp.ui.main.blog.viewmodel.setIsAuthorOfBlogPost
import com.example.mymviapp.util.DateUtils
import kotlinx.android.synthetic.main.fragment_view_blog.*
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class ViewBlogFragment : BaseBlogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscriberObservers()
        checkIsAuthorOfBlogPost()
        stateChangeListener.expandAppBar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (viewModel.isAuthorOfBlogPost()) {
            inflater.inflate(R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.isAuthorOfBlogPost()) {
            when (item.itemId) {
                R.id.edit -> {
                    navUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscriberObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.getContentIfNotHandled()?.let { viewState ->
                    viewModel.setIsAuthorOfBlogPost(
                        viewState.viewBlogFields.isAuthorOfBlog
                    )
                }
            }

        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.viewBlogFields.blogPost?.let { blogPost ->
                setBlogProperties(blogPost)
                if (viewState.viewBlogFields.isAuthorOfBlog) {
                    adaptViewToAuthorMode()
                }
            }
        })
    }

    private fun adaptViewToAuthorMode() {
        activity?.invalidateOptionsMenu()
        delete_button.visibility = View.VISIBLE
    }

    private fun checkIsAuthorOfBlogPost() {
        viewModel.setIsAuthorOfBlogPost(false) //reset
        viewModel.setStateEvent(BlogStateEvent.CheckAuthorOfBlogPost())
    }

    private fun setBlogProperties(blogPost: BlogPost) {
        requestManager
            .load(blogPost.image)
            .into(blog_image)
        blog_title.setText(blogPost.title)
        blog_author.setText(blogPost.username)
        blog_update_date.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))
        blog_body.setText(blogPost.body)
    }

    fun navUpdateBlogFragment() {
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }

}