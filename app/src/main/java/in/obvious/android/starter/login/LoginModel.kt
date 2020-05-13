package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.PasswordBlank
import `in`.obvious.android.starter.login.InputValidationError.UsernameBlank

data class LoginModel(
    val username: String,
    val password: String,
    val validationErrors: Set<InputValidationError>,
    val isLoggingIn : Boolean
) {

    companion object {
        fun create(): LoginModel = LoginModel(
            username = "",
            password = "",
            validationErrors = emptySet(),
            isLoggingIn = false
        )
    }

    fun usernameChanged(username: String): LoginModel {
        return copy(
            username = username,
            validationErrors = validationErrors - UsernameBlank
        )
    }

    fun passwordChanged(password: String): LoginModel {
        return copy(
            password = password,
            validationErrors = validationErrors - PasswordBlank
        )
    }

    fun validationFailed(errors: Set<InputValidationError>): LoginModel {
        return copy(validationErrors = errors)
    }

    fun loggingIn(): LoginModel {
        return copy(isLoggingIn = true)
    }


}
