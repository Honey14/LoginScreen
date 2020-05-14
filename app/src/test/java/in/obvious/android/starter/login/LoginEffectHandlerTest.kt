package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.*
import com.google.common.truth.Truth.*
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.test.RecordingConsumer
import org.junit.Test

class LoginEffectHandlerTest {

    @Test
    fun `when the validate input effect is received, validate the username input without recording consumer`() {
        // given
        val effectHandler = LoginEffectHandler()

        val receivedEvents = mutableListOf<LoginEvent>()

        val connection = effectHandler.connect(object : Consumer<LoginEvent> {
            override fun accept(value: LoginEvent) {
                receivedEvents.add(value)
            }
        })

        // when
        val effect = ValidateInput(username = "", password = "1234")
        connection.accept(effect)

        // then
        val expectedValidationErrors = setOf(UsernameBlank)
        assertThat(receivedEvents).contains(ValidationFailed(expectedValidationErrors))
    }

    @Test
    fun `when the validate input effect is received, validate the entered username`() {
        // given
        val effectHandler = LoginEffectHandler()

        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        // when
        val effect = ValidateInput(username = "", password = "1234")
        connection.accept(effect)

        // then
        val expectedValidationErrors = setOf(UsernameBlank)
        receivedEvents.assertValues(ValidationFailed(expectedValidationErrors))
    }

    @Test
    fun `when the validate input effect is received, validate the entered password`() {
        // given
        val effectHandler = LoginEffectHandler()

        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        // when
        val effect = ValidateInput(username = "vinay", password = "")
        connection.accept(effect)

        // then
        val expectedValidationErrors = setOf(PasswordBlank)
        receivedEvents.assertValues(ValidationFailed(expectedValidationErrors))
    }

    @Test
    fun `when the validate input effect is received, validate the entered username and password are not blank`() {
        // given
        val effectHandler = LoginEffectHandler()

        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        // when
        val effect = ValidateInput(username = "", password = "")
        connection.accept(effect)

        // then
        val expectedValidationErrors = setOf(UsernameBlank, PasswordBlank)
        receivedEvents.assertValues(ValidationFailed(expectedValidationErrors))
    }

    @Test
    fun `when the validate input effect is received, validate the entered username and password`() {
        // given
        val effectHandler = LoginEffectHandler()

        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        // when
        val effect = ValidateInput(username = "vinay", password = "1234")
        connection.accept(effect)

        // then
        receivedEvents.assertValues(ValidationSucceeded)
    }
}
