package com.example.mymviapp.ui.main.blog.viewmodel

import com.example.mymviapp.models.BlogPost
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
fun BlogViewModel.setQuery(query: String) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.searchQuery = query
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.setBlogPost(blogPost: BlogPost) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.blogPost = blogPost
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.setIsAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.isAuthorOfBlog = isAuthorOfBlogPost
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryExhausted = isExhausted
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.setQueryInProgress(isInProgress: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryInProgress = isInProgress
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.setBlogListData(blogList: List<BlogPost>) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.blogList = blogList
    setViewState(update)
}