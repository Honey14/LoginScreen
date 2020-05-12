package `in`.obvious.android.starter.login

data class LoginModel(
    val username: String,
    val password: String
) {

    companion object {
        fun create(): LoginModel = LoginModel(username = "", password = "")
    }

    fun usernameChanged(username: String): LoginModel {
        return copy(username = username)
    }

    fun passwordChanged(password: String): LoginModel {
        return copy(password = password)
    }
}
