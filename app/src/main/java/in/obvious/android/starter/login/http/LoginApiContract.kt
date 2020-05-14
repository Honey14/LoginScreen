package `in`.obvious.android.starter.login.http

import java.io.IOException

data class LoginResponse(val authToken: String)

data class HttpException(
    val code: Int,
    val body: String? = null
): IOException()
