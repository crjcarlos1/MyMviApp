package com.example.mymviapp.ui.main.create_blog.state

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val CREATE_BLOG_VIEW_STATE_BUNDLE_KEY =
    "com.example.mymviapp.ui.main.create_blog.state.CreateBlogViewState"

@Parcelize
data class CreateBlogViewState(
    //createBlogFragment variables
    var blogFields: NewBlogFields = NewBlogFields()
) : Parcelable {

    @Parcelize
    data class NewBlogFields(
        var newBlogTitle: String? = null,
        var newBlogBody: String? = null,
        var newImageUri: Uri? = null
    ) : Parcelable

}