package `in`.obvious.android.starter.login

sealed class LoginEvent

data class UsernameChanged(val username: String) : LoginEvent()

data class PasswordChanged(val password: String) : LoginEvent()
