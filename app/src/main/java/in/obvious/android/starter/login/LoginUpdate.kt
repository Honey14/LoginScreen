package `in`.obvious.android.starter.login

import com.spotify.mobius.Next
import com.spotify.mobius.Next.noChange
import com.spotify.mobius.Update

class LoginUpdate: Update<LoginModel, LoginEvent, LoginEffect> {

    override fun update(
        model: LoginModel,
        event: LoginEvent
    ): Next<LoginModel, LoginEffect> {
        return noChange()
    }
}
