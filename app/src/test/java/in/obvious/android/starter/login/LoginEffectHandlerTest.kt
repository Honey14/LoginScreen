package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.login.InputValidationError.PasswordBlank
import `in`.obvious.android.starter.login.InputValidationError.UsernameBlank
import `in`.obvious.android.starter.login.database.SavingUser
import `in`.obvious.android.starter.login.database.UserDao
import `in`.obvious.android.starter.login.http.FakeLoginApiService
import `in`.obvious.android.starter.login.http.HttpException
import `in`.obvious.android.starter.login.http.LoginApiService
import `in`.obvious.android.starter.login.http.LoginResponse
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
        val service = FakeLoginApiService(otherException = SocketTimeoutException())
        setupConnection(apiService = service)

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
        setupConnection(apiService = service)

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
        setupConnection(apiService = service)

        // when
        val effect = LogIn(username = "vinay", password = "123")
        connection.accept(effect)

        // then
        receivedEvents.assertValues(LoginSucceeded as LoginEvent)
    }

    @Test
    fun `when the Save User effect is received, save the user in database`() {
        setupConnection()

        //when
        val effect = SaveUser(username = "vinay", authToken = "ty67ty65756y")
        connection.accept(effect)

        //then
        receivedEvents.assertValues(UserSaved)
    }

    private fun setupConnection(
        apiService: LoginApiService = FakeLoginApiService(),
        userDao: UserDao = FakeUserDao(),
        uiActions: UiActions = FakeUiActions()
    ) {
        val effectHandler = LoginEffectHandler(apiService, userDao, uiActions)

        connection = effectHandler.connect(receivedEvents)
    }
}

class FakeUserDao : UserDao {
    override fun insertUser(user: SavingUser) {}
}

class FakeUiActions : UiActions {
    override fun navigateToHomeScreen() {}
}
