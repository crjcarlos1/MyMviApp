package com.example.mymviapp.api.main

import androidx.lifecycle.LiveData
import com.example.mymviapp.api.GenericResponse
import com.example.mymviapp.models.AccountProperties
import com.example.mymviapp.util.GenericApiResponse
import retrofit2.http.*

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): LiveData<GenericApiResponse<GenericResponse>>

}