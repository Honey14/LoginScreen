package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.PasswordBlank
import `in`.obvious.android.starter.login.InputValidationError.UsernameBlank
import com.spotify.mobius.Connectable
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer

class LoginEffectHandler : Connectable<LoginEffect, LoginEvent> {

    override fun connect(
        events: Consumer<LoginEvent>
    ): Connection<LoginEffect> {
        return object : Connection<LoginEffect> {

            override fun accept(effect: LoginEffect) {
                when (effect) {
                    is ValidateInput -> validateInput(effect, events)
                }
            }

            override fun dispose() {
                // Nothing to do here
            }
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
