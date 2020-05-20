package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.PasswordBlank
import `in`.obvious.android.starter.login.InputValidationError.UsernameBlank
import `in`.obvious.android.starter.login.database.SavingUser
import `in`.obvious.android.starter.login.database.UserDao
import `in`.obvious.android.starter.login.http.HttpException
import `in`.obvious.android.starter.login.http.LoginApiService
import com.spotify.mobius.Connectable
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import java.io.IOException


class LoginEffectHandler(
    private val loginApiService: LoginApiService,
    private val userDao: UserDao,
    private val uiActions: UiActions
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
                    is GoHome -> uiActions.navigateToHomeScreen()
                }
            }

            override fun dispose() {
                // Nothing to do here
            }
        }
    }

    private fun saveUsername(effect: SaveUser, events: Consumer<LoginEvent>) {
        val username = effect.username

        userDao.insertUser(SavingUser(0, username = username, authToken = effect.authToken))

        events.accept(UserSaved)
    }

    private fun loginAPI(effect: LogIn, events: Consumer<LoginEvent>) {
        val username = effect.username
        val password = effect.password
        try {
            val response = loginApiService.login(username, password)
            events.accept(LoginSucceeded(response.authToken))
        } catch (ex: HttpException) {
            events.accept(IncorrectCredentialsEntered)
        } catch (e: IOException) {
            events.accept(RequestFailedWithNetworkError)
        }
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
