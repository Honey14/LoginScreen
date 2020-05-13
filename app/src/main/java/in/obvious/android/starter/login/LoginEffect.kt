package `in`.obvious.android.starter.login

sealed class LoginEffect

data class ValidateInput(val username : String, val password : String) : LoginEffect()

data class LogIn(val username: String, val password: String) : LoginEffect()