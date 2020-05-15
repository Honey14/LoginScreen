package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.*
import `in`.obvious.android.starter.login.database.SavingUser
import `in`.obvious.android.starter.login.database.UserDaoFake
import `in`.obvious.android.starter.login.http.FakeLoginApiService
import `in`.obvious.android.starter.login.http.HttpException
import `in`.obvious.android.starter.login.http.LoginResponse
import com.google.common.truth.Truth.*
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.test.RecordingConsumer
import org.junit.Test
import java.net.SocketTimeoutException

class LoginEffectHandlerTest {

    @Test
    fun `when the validate input effect is received, validate the username input without recording consumer`() {
        // given
        val effectHandler = LoginEffectHandler(FakeLoginApiService())

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
        val effectHandler = LoginEffectHandler(FakeLoginApiService())

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
        val effectHandler = LoginEffectHandler(FakeLoginApiService())

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
        val effectHandler = LoginEffectHandler(FakeLoginApiService())

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
        val effectHandler = LoginEffectHandler(FakeLoginApiService())

        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        // when
        val effect = ValidateInput(username = "vinay", password = "1234")
        connection.accept(effect)

        // then
        receivedEvents.assertValues(ValidationSucceeded)
    }

    @Test
    fun `when the login effect is received, emit the network failed event if the login call fails`() {
        // given
        val service = FakeLoginApiService(otherException = SocketTimeoutException())

        val effectHandler = LoginEffectHandler(loginApiService = service)
        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        // when
        val effect = LogIn(username = "vinay", password = "123")
        connection.accept(effect)

        // then
        receivedEvents.assertValues(RequestFailedWithNetworkError as LoginEvent)
    }

    @Test
    fun `when the login effect is received, emit the incorrect credentials event if the login call fails`() {
        // given
        val service = FakeLoginApiService(httpException = HttpException(400, "Bad Request"))
        val effectHandler = LoginEffectHandler(loginApiService = service)
        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        // when
        val effect = LogIn(username = "vinay", password = "123")
        connection.accept(effect)

        // then
        receivedEvents.assertValues(IncorrectCredentialsEntered as LoginEvent)
    }

    @Test
    fun `when the login effect is received, emit the validation successful event if the login call succeeds`() {
        // given
        val service = FakeLoginApiService(response = LoginResponse("546tyt74584yty95649yht"))
        val effectHandler = LoginEffectHandler(loginApiService = service)
        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        // when
        val effect = LogIn(username = "vinay", password = "123")
        connection.accept(effect)

        // then
        receivedEvents.assertValues(LoginSucceeded as LoginEvent)
    }

    @Test
    fun `when the Save User effect is received, save the user in database`() {
        val effectHandler = LoginEffectHandler(FakeLoginApiService(), object : UserDaoFake {
            override fun insertUser(user: SavingUser) {

            }
        })

        val receivedEvents = RecordingConsumer<LoginEvent>()
        val connection = effectHandler.connect(receivedEvents)

        //when
        val effect = SaveUser(username = "vinay",authToken = "ty67ty65756y")
        connection.accept(effect)

        //then
        receivedEvents.assertValues(UserSaved)
    }



}
