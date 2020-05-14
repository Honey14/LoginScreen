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
            is IncorrectCredentialsEntered -> next(model.incorrectCredentials())
            is RequestFailedWithNetworkError -> next(model.requestFailed())
            is LoginSucceeded -> dispatch(setOf(SaveUser(model.username))) // not sure why setOf is a compulsion when only username is needed and not a set
            is UserSaved -> dispatch(setOf(GoHome)) // ERROR : will not work without a collection of set Solution: create singleton instead
        }
    }
}
