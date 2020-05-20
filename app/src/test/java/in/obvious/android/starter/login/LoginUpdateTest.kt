package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.PasswordBlank
import `in`.obvious.android.starter.login.InputValidationError.UsernameBlank
import com.spotify.mobius.test.NextMatchers.*
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import org.junit.Test

class LoginUpdateTest {

    private val spec = UpdateSpec(LoginUpdate())

    private val defaultModel = LoginModel.create()

    private val model = defaultModel
        .usernameChanged("honey")
        .passwordChanged("1234")

    @Test
    fun `when the user changes the username, the UI should be updated`() {
        val username = "vinay"

        spec
            .given(defaultModel)
            .whenEvent(UsernameChanged(username))
            .then(
                assertThatNext(
                    hasModel(defaultModel.usernameChanged(username))
                )
            )
    }

    @Test
    fun `when the user changes the password, the UI should be updated`() {
        val password = "password123"

        spec
            .given(defaultModel)
            .whenEvent(PasswordChanged(password))
            .then(
                assertThatNext(
                    hasModel(defaultModel.passwordChanged(password))
                )
            )
    }

    @Test
    fun `when the submit button is clicked, validate the input`() {
        spec
            .given(model)
            .whenEvent(SubmitClicked())
            .then(
                assertThatNext(
                    hasNoModel(),
                    hasEffects(ValidateInput("honey", "1234") as LoginEffect)
                )
            )
    }

    @Test
    fun `when the input validation fails, the errors must be displayed`() {
        val model = defaultModel
            .usernameChanged("")
            .passwordChanged("")

        val errors = setOf(UsernameBlank, PasswordBlank)
        spec
            .given(model)
            .whenEvent(ValidationFailed(errors))
            .then(
                assertThatNext(
                    hasModel(model.validationFailed(errors)),
                    hasNoEffects()
                )
            )
    }

    @Test
    fun `whenever the validation succeeded, then user should be logged in`() {
        spec
            .given(model)
            .whenEvent(ValidationSucceeded)
            .then(
                assertThatNext(
                    hasModel(model.loggingIn()),
                    hasEffects(LogIn("honey", "1234") as LoginEffect)
                )
            )
    }

    @Test
    fun `when incorrect credentials entered, error must be displayed`() {
        spec
            .given(model)
            .whenEvent(IncorrectCredentialsEntered)
            .then(
                assertThatNext(
                    hasModel(model.incorrectCredentials()),
                    hasNoEffects()
                )
            )
    }

    @Test
    fun `when request failed with network error, then error must be displayed `() {
        spec
            .given(model)
            .whenEvent(RequestFailedWithNetworkError)
            .then(
                assertThatNext(
                    hasModel(model.requestFailed())
                )
            )
    }

    @Test
    fun `when login succeeded, then save the user`(){
        val authToken = "65yt65yt56"
        spec
            .given(model)
            .whenEvent(LoginSucceeded(authToken))
            .then(
                assertThatNext(
                    hasNoModel(),
                    hasEffects(SaveUser("honey", authToken) as LoginEffect)
                )
            )
    }

    @Test
    fun `when user saved, then go to home screen`(){ // not sure why do we need to test this flow
        spec
            .given(model)
            .whenEvent(UserSaved)
            .then(
                assertThatNext(
                    hasNoModel(),
                    hasEffects(GoHome as LoginEffect)
                )
            )
    }
}
