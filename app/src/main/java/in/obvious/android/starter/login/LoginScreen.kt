package `in`.obvious.android.starter.login

import `in`.obvious.android.starter.R
import `in`.obvious.android.starter.login.InputValidationError.*
import `in`.obvious.android.starter.login.database.LocalUserDao
import `in`.obvious.android.starter.login.http.LocalLoginApi
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spotify.mobius.Connection
import com.spotify.mobius.Mobius
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.android.MobiusAndroid
import com.spotify.mobius.functions.Consumer
import kotlinx.android.synthetic.main.screen_login.*
import kotlinx.android.synthetic.main.screen_login.view.*

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
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.screen_login, container, false)
        controller.connect { events ->
            connectEvents(view, events)
        }
        return view
    }

    private fun connectEvents(view: View, events: Consumer<LoginEvent>): Connection<LoginModel> {
        // Set up event listeners
        view.usernameTextField.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                events.accept(UsernameChanged(s.toString()))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        view.passwordTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                events.accept(PasswordChanged(s.toString()))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        view.submitButton.setOnClickListener {
            events.accept(SubmitClicked())
        }

        return object : Connection<LoginModel> {

            override fun accept(model: LoginModel) {
                render(model)
                view.text_user.text = model.username
            }

            override fun dispose() {
                // Clear event listeners
                view.submitButton.setOnClickListener(null)
                view.usernameTextField.addTextChangedListener(null)
                view.passwordTextField.addTextChangedListener(null)

            }
        }
    }

    private fun render(model: LoginModel) {
        // Render UI
        if (UsernameBlank in model.validationErrors) {
            usernameTextField.error = "Username cannot be blank!"
        } else {
            usernameTextField.error = null
        }

        if (PasswordBlank in model.validationErrors) {
            passwordTextField.error = "Password cannot be blank"
        } else {
            passwordTextField.error = null
        }
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

    override fun navigateToHomeScreen() {
        // Do navigation here
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, HomeScreen())
            .commit()
    }
}
