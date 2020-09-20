package com.example.mymviapp.ui.main.blog.state

import android.net.Uri
import com.example.mymviapp.models.BlogPost
import com.example.mymviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.example.mymviapp.persistence.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED

data class BlogViewState(
    //BlogFragment variable
    var blogFields: BlogFields = BlogFields(),

    //ViewBlogFragment variables
    var viewBlogFields: ViewBlogFileds = ViewBlogFileds(),

    //UpdateBlogFragment variables
    var updateBlogFields: UpdateBlogFields = UpdateBlogFields()
) {

    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var filter: String = ORDER_BY_ASC_DATE_UPDATED,
        var order: String = BLOG_ORDER_ASC
    )

    data class ViewBlogFileds(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlog: Boolean = false
    )

    data class UpdateBlogFields(
        var updateBlogTitle: String? = null,
        var updateBlogBody: String? = null,
        var updateImageUri: Uri? = null
    )

}