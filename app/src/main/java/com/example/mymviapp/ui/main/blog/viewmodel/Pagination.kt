package com.example.mymviapp.ui.main.blog.viewmodel

import android.util.Log
import com.example.mymviapp.ui.main.blog.state.BlogStateEvent
import com.example.mymviapp.ui.main.blog.state.BlogViewState
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
fun BlogViewModel.resetPage() {
    val update = getCurrentViewStateOrNew()
    update.blogFields.page = 1
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.refreshFromCache(){
    setQueryInProgress(true)
    setQueryExhausted(false)
    setStateEvent(BlogStateEvent.RestoreBlogListFromCache())
}

@InternalCoroutinesApi
fun BlogViewModel.loadFirstPage() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    setStateEvent(BlogStateEvent.BlogSearchEvent())
}

@InternalCoroutinesApi
fun BlogViewModel.incrementPageNumber() {
    val update = getCurrentViewStateOrNew()
    val page = update.copy().blogFields.page
    update.blogFields.page = page + 1
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.nextPage() {
    if (!getIsQueryExhausted() && !getIsQueryInProgress()) {
        Log.d(TAG, "BlogViewModel: Attempting to load next page...")
        incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(BlogStateEvent.BlogSearchEvent())
    }
}

@InternalCoroutinesApi
fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState) {
    setQueryExhausted(viewState.blogFields.isQueryExhausted)
    setQueryInProgress(viewState.blogFields.isQueryInProgress)
    setBlogListData(viewState.blogFields.blogList)
}