package com.example.mymviapp.ui.main.blog


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mymviapp.R
import com.example.mymviapp.models.BlogPost
import com.example.mymviapp.ui.AreYouSureCallback
import com.example.mymviapp.ui.UIMessage
import com.example.mymviapp.ui.UIMessageType
import com.example.mymviapp.ui.main.blog.state.BlogStateEvent
import com.example.mymviapp.ui.main.blog.viewmodel.*
import com.example.mymviapp.util.DateUtils
import com.example.mymviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
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

        delete_button.setOnClickListener {
            confirmDeleteRequest()
        }
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
            if (dataState != null) {
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let { data ->
                    data.data?.getContentIfNotHandled()?.let { viewState ->
                        viewModel.setIsAuthorOfBlogPost(
                            viewState.viewBlogFields.isAuthorOfBlog
                        )
                    }
                    data.response?.peekContent()?.let { response ->
                        if (response.message.equals(SUCCESS_BLOG_DELETED)) {
                            viewModel.removeDeletedBlogPost()
                            findNavController().popBackStack()
                        }
                    }
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

    private fun deleteBlogPost() {
        viewModel.setStateEvent(BlogStateEvent.DeleteBlogPostEvent())
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
        dependencyProvider.getGlideRequestManager()
            .load(blogPost.image)
            .into(blog_image)
        blog_title.setText(blogPost.title)
        blog_author.setText(blogPost.username)
        blog_update_date.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))
        blog_body.setText(blogPost.body)
    }

    private fun confirmDeleteRequest() {
        val callback: AreYouSureCallback = object : AreYouSureCallback {
            override fun proceed() {
                deleteBlogPost()
            }

            override fun cancel() {
                //ignore
            }
        }
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                getString(R.string.are_you_sure_delete),
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    fun navUpdateBlogFragment() {
        try {
            //prep for net fragment
            viewModel.setUpdatedBlogFields(
                viewModel.getBlogPost().title,
                viewModel.getBlogPost().body,
                viewModel.getBlogPost().image.toUri()
            )
            findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
        } catch (e: Exception) {
            //send error report or something. These fields should never be null. Not possible
            Log.e(TAG, "Exception: ${e.message}")
        }
    }

}