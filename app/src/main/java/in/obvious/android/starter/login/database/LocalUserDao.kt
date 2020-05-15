package `in`.obvious.android.starter.login.database

import `in`.obvious.android.starter.throwIfMainThread
import android.util.Log

class LocalUserDao: UserDao {

    override fun insertUser(user: SavingUser) {
        throwIfMainThread()
        Thread.sleep(200L)
        Log.i("Login", "Saved user $user")
    }
}
