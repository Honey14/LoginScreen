package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.R
import `in`.obvious.android.starter.login.database.LocalUserDao
import `in`.obvious.android.starter.login.http.LocalLoginApi
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spotify.mobius.Connection
import com.spotify.mobius.Mobius
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.android.MobiusAndroid
import com.spotify.mobius.functions.Consumer

class LoginScreen : Fragment(), UiActions {

    private val effectHandler = LoginEffectHandler(
        loginApiService = LocalLoginApi(),
        userDao = LocalUserDao(),
        uiActions = this
    )

    private val loop: MobiusLoop.Builder<LoginModel, LoginEvent, LoginEffect> =
        Mobius.loop(LoginUpdate(), effectHandler)

    private val controller: MobiusLoop.Controller<LoginModel, LoginEvent> =
        MobiusAndroid.controller(loop, LoginModel.create())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return createView(inflater, container).also {
            controller.connect(::connectEvents)
        }
    }

    private fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View {
        return inflater.inflate(R.layout.screen_login, container, false)
    }

    private fun connectEvents(events: Consumer<LoginEvent>): Connection<LoginModel> {
        // Set up event listeners
//        submitButton.setOnClickListener { events.accept(SubmitClicked()) }

        return object : Connection<LoginModel> {

            override fun accept(model: LoginModel) {
                render(model)
            }

            override fun dispose() {
                // Clear event listeners
                // submitButton.setOnClickListenrs(null)
            }
        }
    }

    private fun render(model: LoginModel) {
        // Render UI
    }

    override fun onResume() {
        super.onResume()
        controller.start()
    }

    override fun onPause() {
        super.onPause()
        controller.stop()
    }

    override fun onDestroyView() {
        controller.disconnect()
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun navigateToHomeScreen() {
        // Do navigation here
    }
}
