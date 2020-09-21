package com.example.mymviapp.ui.main.create_blog.state

import android.net.Uri

data class CreateBlogViewState(
    //createBlogFragment variables
    var blogFields: NewBlogFields = NewBlogFields()
) {

    data class NewBlogFields(
        var newBlogTitle: String? = null,
        var newBlogBody: String? = null,
        var newImageUri: Uri? = null
    )

}