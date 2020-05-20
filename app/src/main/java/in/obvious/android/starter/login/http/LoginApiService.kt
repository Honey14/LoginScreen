package `in`.obvious.android.starter.login.http

import java.io.IOException

interface LoginApiService {

    @Throws(IOException::class)
    fun login(
        username: String,
        password: String
    ): LoginResponse
}
