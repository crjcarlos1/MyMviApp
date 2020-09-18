package com.example.mymviapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mymviapp.api.GenericResponse
import com.example.mymviapp.api.main.OpenApiMainService
import com.example.mymviapp.api.main.responses.BlogListSearchResponse
import com.example.mymviapp.models.AuthToken
import com.example.mymviapp.models.BlogPost
import com.example.mymviapp.persistence.BlogPostDao
import com.example.mymviapp.persistence.returnOrderedBlogQuery
import com.example.mymviapp.repository.JobManager
import com.example.mymviapp.repository.NetworkBoundResource
import com.example.mymviapp.session.SessionManager
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.main.blog.state.BlogViewState
import com.example.mymviapp.util.AbsentLiveData
import com.example.mymviapp.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.example.mymviapp.util.DateUtils
import com.example.mymviapp.util.GenericApiResponse
import com.example.mymviapp.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@InternalCoroutinesApi
class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {

    private val TAG = "AppDebug"

    fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    //finish by viewing  the db cache
                    result.addSource(loadFromCache()) { viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        if (page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size) {
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<BlogListSearchResponse>) {
                val blogPostList: ArrayList<BlogPost> = ArrayList()
                for (blogPostResponse in response.body.results) {
                    blogPostList.add(
                        BlogPost(
                            pk = blogPostResponse.pk,
                            title = blogPostResponse.title,
                            slug = blogPostResponse.slug,
                            body = blogPostResponse.body,
                            image = blogPostResponse.image,
                            date_updated = DateUtils.convertServerStringDateToLong(blogPostResponse.date_updated),
                            username = blogPostResponse.username
                        )
                    )
                }
                updateLocalDb(blogPostList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openApiMainService.searchListBlogPosts(
                    "Token ${authToken.token!!}",
                    query = query,
                    ordering = filterAndOrder,
                    page = page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.returnOrderedBlogQuery(
                    query = query,
                    filterAndOrder = filterAndOrder,
                    page = page
                )
                    .switchMap {
                        object : LiveData<BlogViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    BlogViewState.BlogFields(
                                        blogList = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObjecj: List<BlogPost>?) {
                if (cacheObjecj != null) {
                    withContext(IO) {
                        for (blogPost in cacheObjecj) {
                            try {
                                //launch each insert as a separate  job to execute  in parallel
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting blog: $blogPost")
                                    blogPostDao.insert(blogPost)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "updateLocalDb: error updating cache on blog post with slug: ${blogPost.slug}"
                                )
                                //optional error handling?
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }
        }.asLiveData()
    }

    fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, BlogViewState>(
            sessionManager.isConnectedToTheInternet(), true, true, false
        ) {

            //ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {
                    Log.d(TAG, "handleApiSuccessResponse: ${response.body.response}")
                    var isAuthor = false
                    if (response.body.response.equals(RESPONSE_HAS_PERMISSION_TO_EDIT)) {
                        isAuthor = true
                    }
                    onCompleteJob(
                        DataState.data(
                            data = BlogViewState(
                                viewBlogFields = BlogViewState.ViewBlogFileds(
                                    isAuthorOfBlog = isAuthor
                                )
                            ),
                            response = null
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.isAuthorOfBlogPost(
                    "Token ${authToken.token}",
                    slug
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            //ignore
            override suspend fun updateLocalDb(cacheObjecj: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("isAuthorOfBlogPost", job)
            }
        }.asLiveData()
    }

}