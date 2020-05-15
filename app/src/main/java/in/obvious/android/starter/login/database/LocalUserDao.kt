package `in`.obvious.android.starter.login.database

import `in`.obvious.android.starter.throwIfMainThread

class LocalUserDao: UserDao {

    override fun insertUser(user: SavingUser) {
        throwIfMainThread()
        Thread.sleep(200L)
    }
}
