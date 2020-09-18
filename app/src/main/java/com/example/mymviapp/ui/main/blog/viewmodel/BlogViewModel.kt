package com.example.mymviapp.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.mymviapp.persistence.BlogQueryUtils
import com.example.mymviapp.repository.main.BlogRepository
import com.example.mymviapp.session.SessionManager
import com.example.mymviapp.ui.BaseViewModel
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.Loading
import com.example.mymviapp.ui.main.blog.state.BlogStateEvent
import com.example.mymviapp.ui.main.blog.state.BlogViewState
import com.example.mymviapp.util.AbsentLiveData
import com.example.mymviapp.util.PreferenceKeys.Companion.BLOG_FILTER
import com.example.mymviapp.util.PreferenceKeys.Companion.BLOG_ORDER
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    init {
        setBlogfilter(
            sharedPreferences.getString(
                BLOG_FILTER,
                BlogQueryUtils.BLOG_FILTER_DATE_UPDATED
            )
        )
        setBlogOrder(
            sharedPreferences.getString(BLOG_ORDER, BlogQueryUtils.BLOG_ORDER_ASC)
        )
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when (stateEvent) {

            is BlogStateEvent.BlogSearchEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        filterAndOrder = getOrder() + getFilter(),
                        page = getPage()
                    )
                } ?: AbsentLiveData.create()
            }

            is BlogStateEvent.CheckAuthorOfBlogPost -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.isAuthorOfBlogPost(
                        authToken = authToken,
                        slug = getSlug()
                    )
                } ?: AbsentLiveData.create()
            }

            is BlogStateEvent.DeleteBlogPostEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.deleteBlogPost(
                        authToken = authToken,
                        blogPost = getBlogPost()
                    )
                } ?: AbsentLiveData.create()
            }

            is BlogStateEvent.None -> {
                return object : LiveData<DataState<BlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }

        }
    }

    override fun initViewState(): BlogViewState {
        return BlogViewState()
    }

    fun saveFilterOptions(filter: String, order: String) {
        editor.putString(BLOG_FILTER, filter)
        editor.apply()

        editor.putString(BLOG_ORDER, order)
        editor.apply()
    }

    fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(BlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}