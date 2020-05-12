package `in`.obvious.android.starter.login

import com.spotify.mobius.test.NextMatchers.hasModel
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
    fun `when the user changes the passord, the UI should be updated`() {
        val password = "password123"

        spec
            .given(defaultModel)
            .whenEvent(PasswordChanged(password))
            .then(assertThatNext(
                hasModel(defaultModel.passwordChanged(password))
            ))
    }
}
