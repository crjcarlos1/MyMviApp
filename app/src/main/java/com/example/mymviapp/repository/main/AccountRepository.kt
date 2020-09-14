package com.example.mymviapp.repository.main

import android.util.Log
import com.example.mymviapp.api.main.OpenApiMainService
import com.example.mymviapp.persistence.AccountPropertiesDao
import com.example.mymviapp.session.SessionManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun cancelActiveJobs(){
        Log.d(TAG, "AuthRepository: cancelling on-joing jobs...")
    }


}