package com.example.mymviapp.di.auth

import com.example.mymviapp.persistence.AccountPropertiesDao
import com.example.mymviapp.persistence.AuthTokenDao
import com.example.mymviapp.api.auth.OpenApiAuthService
import com.example.mymviapp.repository.auth.AuthRepository
import com.example.mymviapp.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.Retrofit

@InternalCoroutinesApi
@Module
class AuthModule{

    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder : Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder.build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }

}