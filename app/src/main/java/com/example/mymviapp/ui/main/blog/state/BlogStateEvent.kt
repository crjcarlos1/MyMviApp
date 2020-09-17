package com.example.mymviapp.ui.main.blog.state

sealed class BlogStateEvent {

    class BlogSearchEvent : BlogStateEvent()
    class CheckAuthorOfBlogPost : BlogStateEvent()
    class None : BlogStateEvent()

}