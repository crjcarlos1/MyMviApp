package com.example.mymviapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mymviapp.api.GenericResponse
import com.example.mymviapp.api.main.OpenApiMainService
import com.example.mymviapp.models.AccountProperties
import com.example.mymviapp.models.AuthToken
import com.example.mymviapp.persistence.AccountPropertiesDao
import com.example.mymviapp.repository.NetworkBoundResource
import com.example.mymviapp.session.SessionManager
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.Response
import com.example.mymviapp.ui.ResponseType
import com.example.mymviapp.ui.main.account.state.AccountViewState
import com.example.mymviapp.util.AbsentLiveData
import com.example.mymviapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
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
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true, false, true
            ) {
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    //finish by viewing the db cache
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(DataState.data(data = viewState, response = null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)
                createCacheRequestAndReturn()
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

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObjecj: AccountProperties?) {
                cacheObjecj?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObjecj.pk,
                        cacheObjecj.email,
                        cacheObjecj.username
                    )
                }
            }
        }.asLiveData()
    }

    fun saveAccountProperties(
        authToken: AuthToken,
        accountProperties: AccountProperties
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToTheInternet(), true, true, false
        ) {
            //not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {
                updateLocalDb(null)
                withContext(Main) {
                    //finish with success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                response.body.response, ResponseType.Toast()
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            //not use in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObjecj: Any?) {
                return accountPropertiesDao.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
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