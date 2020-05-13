package `in`.obvious.android.starter.login

import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update

class LoginUpdate : Update<LoginModel, LoginEvent, LoginEffect> {

    override fun update(
        model: LoginModel,
        event: LoginEvent
    ): Next<LoginModel, LoginEffect> {
        return when (event) {
            is UsernameChanged -> next(model.usernameChanged(event.username))
            is PasswordChanged -> next(model.passwordChanged(event.password))
            is SubmitClicked -> dispatch(setOf(ValidateInput(model.username, model.password)))
            is ValidationFailed -> next(model.validationFailed(event.errors))
            is ValidationSucceeded -> next(
                model.loggingIn(),
                setOf(LogIn(model.username, model.password))
            )
            is IncorrectCredentialsEntered -> next(model.incorrectCredentials(event.error))
        }
    }
}
