package vn.tapbi.message

import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import vn.tapbi.message.utils.MyDebugTree

@HiltAndroidApp
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initLog()
        instance = this
    }


    private fun initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(MyDebugTree())
        }
    }

    companion object {
        var instance: App? = null
            private set
    }
}