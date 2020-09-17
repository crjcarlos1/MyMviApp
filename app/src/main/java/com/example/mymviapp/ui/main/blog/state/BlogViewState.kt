package com.example.mymviapp.ui.main.blog.state

import com.example.mymviapp.models.BlogPost

data class BlogViewState(
    //BlogFragment variable
    var blogFields: BlogFields = BlogFields()

    //ViewBlogFragment variables


    //UpdateBlogFragment variables
) {

    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = ""
    )

}