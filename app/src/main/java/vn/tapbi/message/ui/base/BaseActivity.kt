package vn.tapbi.message.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }
}
