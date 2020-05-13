package `in`.obvious.android.starter.login

data class LoginModel(
    val username: String,
    val password: String,
    val validationErrors: Set<InputValidationError>
) {

    companion object {
        fun create(): LoginModel = LoginModel(
            username = "",
            password = "",
            validationErrors = emptySet()
        )
    }

    fun usernameChanged(username: String): LoginModel {
        return copy(username = username)
    }

    fun passwordChanged(password: String): LoginModel {
        return copy(password = password)
    }

    fun validationFailed(errors: Set<InputValidationError>): LoginModel {
        return copy(validationErrors = errors)
    }
}
