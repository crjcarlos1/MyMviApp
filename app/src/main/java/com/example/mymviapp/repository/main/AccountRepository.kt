package com.example.mymviapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.mymviapp.api.main.OpenApiMainService
import com.example.mymviapp.models.AccountProperties
import com.example.mymviapp.models.AuthToken
import com.example.mymviapp.persistence.AccountPropertiesDao
import com.example.mymviapp.repository.NetworkBoundResource
import com.example.mymviapp.session.SessionManager
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.main.account.state.AccountViewState
import com.example.mymviapp.util.GenericApiResponse
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import javax.inject.Inject

@InternalCoroutinesApi
class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<AccountProperties, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true
        ) {
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {

            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties(
                    "Token ${authToken.token}"
                )
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.asLiveData()
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "AuthRepository: cancelling on-joing jobs...")
    }


}