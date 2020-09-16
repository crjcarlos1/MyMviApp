package com.example.mymviapp.ui.auth.state

sealed class AuthStateEvent {

    data class LoginAttempEvent(
        val email: String,
        val password: String
    ) : AuthStateEvent()

    data class RegisterAttempEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirmPassword: String
    ) : AuthStateEvent()

    class CheckPreviousAuthEvent() : AuthStateEvent()

    class None : AuthStateEvent()

}