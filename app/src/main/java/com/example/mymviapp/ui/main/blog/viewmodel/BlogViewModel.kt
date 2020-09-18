package com.example.mymviapp.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.example.mymviapp.repository.main.BlogRepository
import com.example.mymviapp.session.SessionManager
import com.example.mymviapp.ui.BaseViewModel
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.Loading
import com.example.mymviapp.ui.main.blog.state.BlogStateEvent
import com.example.mymviapp.ui.main.blog.state.BlogViewState
import com.example.mymviapp.util.AbsentLiveData
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val requestManager: RequestManager
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when (stateEvent) {
            is BlogStateEvent.BlogSearchEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        page = getPage()
                    )
                } ?: AbsentLiveData.create()
            }
            is BlogStateEvent.CheckAuthorOfBlogPost -> {
                return AbsentLiveData.create()
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