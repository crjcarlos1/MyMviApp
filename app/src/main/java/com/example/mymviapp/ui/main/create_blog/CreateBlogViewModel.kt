package com.example.mymviapp.ui.main.create_blog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.mymviapp.repository.main.CreateBlogRepository
import com.example.mymviapp.session.SessionManager
import com.example.mymviapp.ui.BaseViewModel
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.Loading
import com.example.mymviapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.example.mymviapp.ui.main.create_blog.state.CreateBlogViewState
import com.example.mymviapp.util.AbsentLiveData
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val createBlogRepository: CreateBlogRepository,
    val sessionManager: SessionManager
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {

    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        when (stateEvent) {

            is CreateBlogStateEvent.CreateNewBlogEvent -> {
                return AbsentLiveData.create()
            }

            is CreateBlogStateEvent.None -> {
                return liveData {
                    emit(DataState(null, Loading(false), null))
                }
            }

        }
    }

    override fun initViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    fun setNewBlogFields(title: String?, body: String?, uri: Uri?) {
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.blogFields
        title?.let { newBlogFields.newBlogTitle = it }
        body?.let { newBlogFields.newBlogBody = it }
        uri?.let { newBlogFields.newImageUri = it }
        update.blogFields = newBlogFields
        setViewState(update)
    }

    fun clearNewBlogFileds() {
        val update = getCurrentViewStateOrNew()
        update.blogFields = CreateBlogViewState.NewBlogFields()
        setViewState(update)
    }

    fun cancelActiveJobs(){
        createBlogRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData() {
        setStateEvent(CreateBlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}