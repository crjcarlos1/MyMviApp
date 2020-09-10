package com.example.mymviapp.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.mymviapp.api.auth.network_responses.LoginResponse
import com.example.mymviapp.api.auth.network_responses.RegistrationResponse
import com.example.mymviapp.models.AuthToken
import com.example.mymviapp.persistence.AccountPropertiesDao
import com.example.mymviapp.persistence.AuthTokenDao
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.Response
import com.example.mymviapp.ui.ResponseType
import com.example.mymviapp.ui.auth.state.AuthViewState
import com.example.mymviapp.ui.auth.state.LoginFields
import com.example.mymviapp.ui.auth.state.RegistrationFields
import com.example.mymviapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.example.mymviapp.util.GenericApiResponse
import com.example.mymviapp.api.auth.OpenApiAuthService
import com.example.mymviapp.repository.NetworkBoundResource
import com.example.mymviapp.session.SessionManager
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import javax.inject.Inject

@InternalCoroutinesApi
class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun attempLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
        if (!loginFieldErrors.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Toast())
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            /**
             * le pasamos el job de NetworkBoundResource a ´repositoryJob´ para el caso en que se quiera cancelar el job desde la UI
             */
            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.asLiveData()

    }

    fun attempRegistration(
        email: String,
        username: String,
        password: String,
        confirmPasword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldErros =
            RegistrationFields(email, username, password, confirmPasword).isValidForRegistration()

        if (!registrationFieldErros.equals(RegistrationFields.RegistrationError.none())) {
            return returnErrorResponse(registrationFieldErros, ResponseType.Dialog())
        }

        return object :
            NetworkBoundResource<RegistrationResponse, AuthViewState>(sessionManager.isConnectedToTheInternet()) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                response.body.pk,
                                response.body.token
                            )
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPasword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()

    }

    fun cancelActiveJobs() {
        Log.d(TAG, "AuthRepository: Cancelling on-going jobs... ")
        repositoryJob?.cancel()
    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(Response(errorMessage, responseType))
            }
        }
    }

    /*
    fun attempLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.login(email, password)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is GenericApiResponse.ApiSuccessResponse -> {
                                value = DataState.data(
                                    AuthViewState(
                                        authToken = AuthToken(response.body.pk, response.body.token)
                                    ),
                                    null
                                )
                            }
                            is GenericApiResponse.ApiErrorResponse -> {
                                value = DataState.error(
                                    Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                            is GenericApiResponse.ApiEmptyResponse -> {
                                value = DataState.error(
                                    Response(
                                        message = ErrorHandling.ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }

    fun attempRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.register(email, username, password, confirmPassword)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is GenericApiResponse.ApiSuccessResponse -> {
                                value = DataState.data(
                                    AuthViewState(
                                        authToken = AuthToken(response.body.pk, response.body.token)
                                    ), null
                                )
                            }
                            is GenericApiResponse.ApiErrorResponse -> {
                                value = DataState.error(
                                    Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                            is GenericApiResponse.ApiEmptyResponse -> {
                                value = DataState.error(
                                    Response(
                                        message = ErrorHandling.ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                        }

                    }
                }
            }
    }

     */


}