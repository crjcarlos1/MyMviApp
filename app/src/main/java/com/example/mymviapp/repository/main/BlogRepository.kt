package com.example.mymviapp.repository.main

import com.example.mymviapp.api.main.OpenApiMainService
import com.example.mymviapp.persistence.BlogPostDao
import com.example.mymviapp.repository.JobManager
import com.example.mymviapp.session.SessionManager
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {

}