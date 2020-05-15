package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.PasswordBlank
import `in`.obvious.android.starter.login.InputValidationError.UsernameBlank
import `in`.obvious.android.starter.login.http.LoginApiService
import com.spotify.mobius.Connectable
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer


class LoginEffectHandler(
    private val loginApiService: LoginApiService
) : Connectable<LoginEffect, LoginEvent> {

    override fun connect(
        events: Consumer<LoginEvent>
    ): Connection<LoginEffect> {
        return object : Connection<LoginEffect> {

            override fun accept(effect: LoginEffect) {
                when (effect) {
                    is ValidateInput -> validateInput(effect, events)
                    is LogIn -> loginAPI(effect, events)
                    is SaveUser -> saveUsername(effect, events)
                }
            }

            override fun dispose() {
                // Nothing to do here
            }
        }
    }

    private fun saveUsername(effect: SaveUser, events: Consumer<LoginEvent>) {
//        val username = effect.username
//        SaveUserDb.getDatabase(application).userDao().insertUser(SavingUser(username))


    }

    private fun loginAPI(effect: LogIn, events: Consumer<LoginEvent>) {
        val username = effect.username
        val password = effect.password

        val loginResult = loginApiService.login(username, password)

        val loginEvent = when {
            loginResult.authToken.isNotEmpty() -> {
                LoginSucceeded
            }
            else -> {
                RequestFailedWithNetworkError // ERROR: not sure how the HTTPException class could have a condition here
                IncorrectCredentialsEntered
            }
        }

        events.accept(loginEvent)
    }

    private fun validateInput(
        effect: ValidateInput,
        events: Consumer<LoginEvent>
    ) {
        val username = effect.username
        val password = effect.password

        val validationErrors = when {
            username.isBlank() && password.isBlank() -> setOf(UsernameBlank, PasswordBlank)
            username.isBlank() -> setOf(UsernameBlank)
            password.isBlank() -> setOf(PasswordBlank)
            else -> emptySet()
        }

        val validationEvent = if (validationErrors.isEmpty())
            ValidationSucceeded
        else
            ValidationFailed(validationErrors)

        events.accept(validationEvent)
    }
}
