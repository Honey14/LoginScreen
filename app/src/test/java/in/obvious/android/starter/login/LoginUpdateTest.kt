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
        val validateModel = defaultModel
            .usernameChanged("honey")
            .passwordChanged(password = "password12")

        spec
            .given(validateModel)
            .whenEvent(SubmitClicked())
            .then(
                assertThatNext(
                    hasNoModel(),
                    hasEffects(ValidateInput("honey", "password12") as LoginEffect)
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
        val model = defaultModel
            .usernameChanged("name")
            .passwordChanged("1234")
        spec
            .given(model)
            .whenEvent(ValidationSucceeded())
            .then(
                assertThatNext(
                    hasModel(model.loggingIn()),
                    hasEffects(LogIn("name", "1234") as LoginEffect)
                )
            )
    }

    @Test
    fun `when incorrect credentials entered, error must be displayed`() {
        val model = defaultModel
            .usernameChanged("honey")
            .passwordChanged("1234")
        val error = "Incorrect credentials, please try again!"
        spec
            .given(model)
            .whenEvent(IncorrectCredentialsEntered(error))
            .then(
                assertThatNext(
                    hasModel(model.incorrectCredentials(error)),
                    hasNoEffects()
                )
            )
    }
}
