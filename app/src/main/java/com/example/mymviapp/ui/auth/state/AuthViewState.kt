package com.example.mymviapp.ui.auth.state

import com.example.mymviapp.models.AuthToken

data class AuthViewState(
    var registrationFileds: RegistrationFields? = RegistrationFields(),
    var loginFields: LoginFields? = LoginFields(),
    var authToken: AuthToken? = null
)

data class RegistrationFields(
    var registrationEmail: String? = null,
    var registrationUsername: String? = null,
    var registrationPassword: String? = null,
    var registrationConfirmPassword: String? = null
) {

    class RegistrationError {
        companion object {
            fun mustFillAllFields(): String {
                return "All fields are required"
            }

            fun passwordsDoNotMatch(): String {
                return "Passwords must match"
            }

            fun none(): String {
                return "None"
            }
        }
    }

    fun isValidForRegistration(): String {
        if (registrationEmail.isNullOrEmpty()
            || registrationUsername.isNullOrEmpty()
            || registrationPassword.isNullOrEmpty()
            || registrationConfirmPassword.isNullOrEmpty()
        ) {
            return RegistrationError.mustFillAllFields()
        }
        if (!registrationPassword.equals(registrationConfirmPassword)) {
            return RegistrationError.passwordsDoNotMatch()
        }
        return RegistrationError.none()
    }
}

data class LoginFields(
    var loginEmail: String? = null,
    var loginPassword: String? = null
) {
    class LoginError {

        companion object {

            fun mustFillAllFields(): String {
                return "You can't login without an email and password."
            }

            fun none(): String {
                return "None"
            }

        }
    }

    fun isValidForLogin(): String {

        if (loginEmail.isNullOrEmpty()
            || loginPassword.isNullOrEmpty()
        ) {

            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginState(email=$loginEmail, password=$loginPassword)"
    }
}