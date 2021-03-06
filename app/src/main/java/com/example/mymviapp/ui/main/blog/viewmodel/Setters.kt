package com.example.mymviapp.ui.main.blog.viewmodel

import android.net.Uri
import android.os.Parcelable
import com.example.mymviapp.models.BlogPost
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
fun BlogViewModel.removeDeletedBlogPost() {
    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList.toMutableList()
    for (i in 0..(list.size - 1)) {
        if (list[i] == getBlogPost()) {
            list.remove(getBlogPost())
            break
        }
    }
    setBlogListData(list)
}

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

@InternalCoroutinesApi
fun BlogViewModel.setBlogfilter(filter: String?) {
    filter?.let {
        val update = getCurrentViewStateOrNew()
        update.blogFields.filter = filter
        setViewState(update)
    }
}

@InternalCoroutinesApi
fun BlogViewModel.setLayoutManagerState(layoutManagerState: Parcelable) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.layoutManagerState = layoutManagerState
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.clearLayoutManagerState() {
    val update = getCurrentViewStateOrNew()
    update.blogFields.layoutManagerState = null
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.setBlogOrder(order: String?) {
    order?.let {
        val update = getCurrentViewStateOrNew()
        update.blogFields.order = order
        setViewState(update)
    }
}

@InternalCoroutinesApi
fun BlogViewModel.setUpdatedBlogFields(
    title: String?,
    body: String?,
    uri: Uri?
) {
    val update = getCurrentViewStateOrNew()
    val updateBlogFields = update.updateBlogFields
    title?.let { updateBlogFields.updateBlogTitle = it }
    body?.let { updateBlogFields.updateBlogBody = it }
    uri?.let { updateBlogFields.updateImageUri = it }
    update.updateBlogFields = updateBlogFields
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.updateListItem(newBlogPost: BlogPost) {
    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList.toMutableList()
    for (i in 0..(list.size - 1)) {
        if (list[i].pk == newBlogPost.pk) {
            list[i] = newBlogPost
            break
        }
    }
    update.blogFields.blogList = list
    setViewState(update)
}

@InternalCoroutinesApi
fun BlogViewModel.onBlogPostUpdateSuccess(blogPost: BlogPost) {
    setUpdatedBlogFields(
        uri = null,
        title = blogPost.title,
        body = blogPost.body
    )// update UpdateBlogFragment (not really ecessary since navigating back)
    setBlogPost(blogPost)// updateViewBlogFragment
    updateListItem(blogPost)//updateBlogFragment
}