package `in`.obvious.android.starter

import `in`.obvious.android.starter.login.LoginScreen
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    supportFragmentManager
      .beginTransaction()
      .add(R.id.fragment, LoginScreen())
      .commit()
  }
}
