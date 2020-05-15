package `in`.obvious.android.starter.login.http

import `in`.obvious.android.starter.throwIfMainThread
import java.net.SocketTimeoutException

class LocalLoginApi: LoginApiService {

    override fun login(username: String, password: String): LoginResponse {
        throwIfMainThread()

        val waitFor = (2..5).random()
        Thread.sleep(waitFor * 1000L)

        return when(username) {
            "vinay" -> throw SocketTimeoutException()
            "honey" -> throw HttpException(400, "Invalid credentials!")
            else -> LoginResponse("access granted")
        }
    }
}
