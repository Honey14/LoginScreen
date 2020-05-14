package `in`.obvious.android.starter.login.http

interface LoginApiService {

    fun login(
        username: String,
        password: String
    ): LoginResponse
}
