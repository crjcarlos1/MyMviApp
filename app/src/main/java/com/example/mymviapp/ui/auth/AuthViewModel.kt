package com.example.mymviapp.ui.auth

import androidx.lifecycle.LiveData
import com.example.mymviapp.models.AuthToken
import com.example.mymviapp.repository.auth.AuthRepository
import com.example.mymviapp.ui.BaseViewModel
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.auth.state.AuthStateEvent
import com.example.mymviapp.ui.auth.state.AuthViewState
import com.example.mymviapp.ui.auth.state.LoginFields
import com.example.mymviapp.ui.auth.state.RegistrationFields
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class AuthViewModel
@Inject
constructor(val authRepository: AuthRepository) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when (stateEvent) {
            is AuthStateEvent.LoginAttempEvent -> {
                return authRepository.attemptLogin(
                    stateEvent.email, stateEvent.password
                )
            }
            is AuthStateEvent.RegisterAttempEvent -> {
                return authRepository.attemptRegistration(
                    stateEvent.email, stateEvent.username,
                    stateEvent.password, stateEvent.confirmPassword
                )
            }
            is AuthStateEvent.CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }
            is AuthStateEvent.None -> {
                return object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    override fun initViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setLoginFileds(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        setViewState(update)
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFileds == registrationFields) {
            return
        }
        update.registrationFileds = registrationFields
        setViewState(update)
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        setViewState(update)
    }

    fun cancelActiveJobs() {
        handlePendingData()
        authRepository.cancelActiveJobs()
    }

    fun handlePendingData() {
        setStateEvent(AuthStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        authRepository.cancelActiveJobs()
    }

}