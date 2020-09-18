package com.example.mymviapp.ui.main.blog.state

import com.example.mymviapp.models.BlogPost

data class BlogViewState(
    //BlogFragment variable
    var blogFields: BlogFields = BlogFields(),

    //ViewBlogFragment variables
    var viewBlogFields: ViewBlogFileds = ViewBlogFileds()

    //UpdateBlogFragment variables
) {

    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )

    data class ViewBlogFileds(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlog: Boolean = false
    )

}