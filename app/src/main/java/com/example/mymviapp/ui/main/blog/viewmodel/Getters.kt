package com.example.mymviapp.ui.main.blog.viewmodel

import android.net.Uri
import com.example.mymviapp.models.BlogPost
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
fun BlogViewModel.getOrder(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.order
    }
}

@InternalCoroutinesApi
fun BlogViewModel.getFilter(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.filter
    }
}

@InternalCoroutinesApi
fun BlogViewModel.getSearchQuery(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.searchQuery
    }
}

@InternalCoroutinesApi
fun BlogViewModel.getPage(): Int {
    getCurrentViewStateOrNew().let {
        return it.blogFields.page
    }
}

@InternalCoroutinesApi
fun BlogViewModel.getIsQueryExhausted(): Boolean {
    getCurrentViewStateOrNew().let {
        return it.blogFields.isQueryExhausted
    }
}

@InternalCoroutinesApi
fun BlogViewModel.getIsQueryInProgress(): Boolean {
    getCurrentViewStateOrNew().let {
        return it.blogFields.isQueryInProgress
    }
}

@InternalCoroutinesApi
fun BlogViewModel.getSlug(): String {
    getCurrentViewStateOrNew().let {
        it.viewBlogFields.blogPost?.let {
            return it.slug
        }
    }
    return ""
}

@InternalCoroutinesApi
fun BlogViewModel.getBlogPost(): BlogPost {
    getCurrentViewStateOrNew().let {
        return it.viewBlogFields.blogPost?.let {
            return it
        } ?: getDummyBlogPost()
    }
}

@InternalCoroutinesApi
fun BlogViewModel.getDummyBlogPost(): BlogPost {
    return BlogPost(-1, "", "", "", "", 1, "")
}

@InternalCoroutinesApi
fun BlogViewModel.isAuthorOfBlogPost(): Boolean {
    getCurrentViewStateOrNew().let {
        return it.viewBlogFields.isAuthorOfBlog
    }
}

@InternalCoroutinesApi
fun BlogViewModel.getUpdatedBlogUri(): Uri? {
    getCurrentViewStateOrNew().let {
        it.updateBlogFields.updateImageUri?.let {
            return it
        }
    }
    return null
}