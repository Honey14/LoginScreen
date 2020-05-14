package `in`.obvious.android.starter.login

sealed class LoginEvent

data class UsernameChanged(val username: String) : LoginEvent()

data class PasswordChanged(val password: String) : LoginEvent()

class SubmitClicked : LoginEvent()

data class ValidationFailed(val errors: Set<InputValidationError>) : LoginEvent()

object ValidationSucceeded : LoginEvent()

data class IncorrectCredentialsEntered(val error: String) : LoginEvent()

data class RequestFailedWithNetworkError(val error: String) : LoginEvent()

class LoginSucceeded : LoginEvent()

class UserSaved : LoginEvent()
