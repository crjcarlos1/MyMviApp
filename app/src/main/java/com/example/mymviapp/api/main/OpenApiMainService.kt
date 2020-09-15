package com.example.mymviapp.api.main

import androidx.lifecycle.LiveData
import com.example.mymviapp.models.AccountProperties
import com.example.mymviapp.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

}