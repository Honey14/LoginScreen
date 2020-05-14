package `in`.obvious.android.starter.login.http

import java.io.IOException

class FakeLoginApiService(
    private val response: LoginResponse? = null,
    private val httpException: HttpException? = null,
    private val otherException: IOException? = null
) : LoginApiService {

    override fun login(
        username: String,
        password: String
    ): LoginResponse {
        if (response != null) return response

        if (httpException != null) throw httpException

        if (otherException != null) throw otherException

        throw RuntimeException("No response configured!")
    }
}
