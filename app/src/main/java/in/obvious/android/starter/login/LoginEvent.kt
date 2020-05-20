package `in`.obvious.android.starter.login

sealed class LoginEvent

data class UsernameChanged(val username: String) : LoginEvent()

data class PasswordChanged(val password: String) : LoginEvent()

class SubmitClicked : LoginEvent()

data class ValidationFailed(val errors: Set<InputValidationError>) : LoginEvent()

object ValidationSucceeded : LoginEvent()

object IncorrectCredentialsEntered : LoginEvent()

object RequestFailedWithNetworkError: LoginEvent()

data class LoginSucceeded(val authToken: String) : LoginEvent()

object UserSaved : LoginEvent()
