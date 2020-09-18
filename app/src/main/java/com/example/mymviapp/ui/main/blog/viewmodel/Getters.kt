package com.example.mymviapp.ui.main.blog.viewmodel

import kotlinx.coroutines.InternalCoroutinesApi

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
