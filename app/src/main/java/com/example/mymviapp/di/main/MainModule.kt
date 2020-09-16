package com.example.mymviapp.di.main

import com.example.mymviapp.api.main.OpenApiMainService
import com.example.mymviapp.persistence.AccountPropertiesDao
import com.example.mymviapp.repository.main.AccountRepository
import com.example.mymviapp.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.Retrofit

@InternalCoroutinesApi
@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(openApiMainService, accountPropertiesDao, sessionManager)
    }

}