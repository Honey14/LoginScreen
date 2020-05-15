package `in`.obvious.android.starter

import android.os.Looper

fun throwIfMainThread() {
    if(Looper.getMainLooper() === Looper.myLooper()) {
        throw RuntimeException("Main thread!")
    }
}
