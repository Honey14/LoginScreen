package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.PasswordBlank
import `in`.obvious.android.starter.login.InputValidationError.UsernameBlank
import `in`.obvious.android.starter.login.database.SavingUser
import `in`.obvious.android.starter.login.database.UserDao
import `in`.obvious.android.starter.login.http.FakeLoginApiService
import `in`.obvious.android.starter.login.http.HttpException
import `in`.obvious.android.starter.login.http.LoginApiService
import `in`.obvious.android.starter.login.http.LoginResponse
import com.nhaarman.mockitokotlin2.*
import com.spotify.mobius.Connection
import com.spotify.mobius.test.RecordingConsumer
import org.junit.After
import org.junit.Test
import java.net.SocketTimeoutException

class LoginEffectHandlerTest {

    private val receivedEvents = RecordingConsumer<LoginEvent>()

    private lateinit var connection: Connection<LoginEffect>

    @After
    fun tearDown() {
        connection.dispose()
    }

    /*@Test
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
    }*/

    @Test
    fun `when the validate input effect is received, validate the entered username`() {
        // given
        setupConnection()

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
        setupConnection()

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
        setupConnection()

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
        setupConnection()

        // when
        val effect = ValidateInput(username = "vinay", password = "1234")
        connection.accept(effect)

        // then
        receivedEvents.assertValues(ValidationSucceeded)
    }

    @Test
    fun `when the login effect is received, emit the network failed event if the login call fails`() {
        // given
//        val service = FakeLoginApiService(otherException = SocketTimeoutException())
        val username = "vinay"
        val password = "123"

        val service = mock<LoginApiService>()
        whenever(service.login(username = eq(username), password = eq(password)))
            .thenThrow(SocketTimeoutException())

        setupConnection(apiService = service)

        // when
        val effect = LogIn(username = username, password = password)
        connection.accept(effect)

        // then
        receivedEvents.assertValues(RequestFailedWithNetworkError as LoginEvent)
    }

    @Test
    fun `when the login effect is received, emit the incorrect credentials event if the login call fails`() {
        // given
        val username = "vinay"
        val password = "123"
//        val service = FakeLoginApiService(httpException = HttpException(400, "Bad Request"))

        val service = mock<LoginApiService>()
        whenever(service.login(username = eq(username), password = eq(password)))
            .thenThrow(HttpException(400, "Bad Request"))

        setupConnection(apiService = service)

        // when
        val effect = LogIn(username = username, password = password)
        connection.accept(effect)

        // then
        receivedEvents.assertValues(IncorrectCredentialsEntered as LoginEvent)
    }

    @Test
    fun `when the login effect is received, emit the validation successful event if the login call succeeds`() {
        // given
        val authToken = "546tyt74584yty95649yht"
        val username = "vinay"
        val password = "123"
        val service = mock<LoginApiService>()
        whenever(service.login(eq(username), eq(password)))
            .thenReturn(LoginResponse(authToken))

//        val service = FakeLoginApiService(response = LoginResponse(authToken))

        setupConnection(apiService = service)

        // when
        val effect = LogIn(username = "vinay", password = "123")
        connection.accept(effect)

        // then
        receivedEvents.assertValues(LoginSucceeded(authToken) as LoginEvent)
    }

    @Test
    fun `when the Save User effect is received, save the user in database`() {
        val dao = mock<UserDao>()
        setupConnection(userDao = dao)

        //when
        val username = "vinay"
        val authToken = "ty67ty65756y"
        val effect = SaveUser(username = username, authToken = authToken)
        connection.accept(effect)

        //then
        verify(dao).insertUser(SavingUser(0, username, authToken))
        verifyNoMoreInteractions(dao)
        receivedEvents.assertValues(UserSaved)
    }

    private fun setupConnection(
        apiService: LoginApiService = FakeLoginApiService(),
        userDao: UserDao = mock(),
        uiActions: UiActions = FakeUiActions()
    ) {
        val effectHandler = LoginEffectHandler(apiService, userDao, uiActions)

        connection = effectHandler.connect(receivedEvents)
    }
}

class FakeUiActions : UiActions {
    override fun navigateToHomeScreen() {}
}
