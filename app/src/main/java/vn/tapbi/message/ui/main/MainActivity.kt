package vn.tapbi.message.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Rect
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.databinding.ActivityMainBinding
import vn.tapbi.message.ui.base.BaseBindingActivity
import vn.tapbi.message.utils.Event


class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onStart() {
        super.onStart()
        lastClickTime = 0
    }

    override fun setupView(savedInstanceState: Bundle?) {
        val mBinding = binding!!
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.conversationsFragment,
                R.id.contactsFragment,
                R.id.settingsFragment
            )
        )
//        mBinding.bnvMain.setupWithNavController(navController)

        mBinding.bnvMain.menu.forEach {
            val view = mBinding.bnvMain.findViewById<View>(it.itemId)
            view.setOnLongClickListener {
                true
            }
        }
        mBinding.bnvMain.setOnItemSelectedListener { itemView ->
            when(itemView.itemId){
                R.id.conversationsFragment -> {
                    if(checkTime()){
                        navController.navigate(R.id.conversationsFragment)
                        return@setOnItemSelectedListener true
                    }
                }
                R.id.contactsFragment -> {
                    if (checkTime()) {
                        navController.navigate(R.id.contactsFragment)
                        return@setOnItemSelectedListener true
                    }
                }
                R.id.settingsFragment -> {
                    if (checkTime()) {
                        navController.navigate(R.id.settingsFragment)
                        return@setOnItemSelectedListener true
                    }
                }
            }
            return@setOnItemSelectedListener false
        }
        checkPermissions()
    }

    private var lastClickTime: Long = 0
    fun checkTime(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 600) {
            return false
        }
        lastClickTime = currentTime
        return true
    }

    private fun checkPermissions() {
        if (!isAllPermissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(Constant.permissions, Constant.REQUEST_CODE_PERMISSIONS)
            } else {
                onPermissionGranted()
            }
        } else {
            onPermissionGranted()
        }
    }

    val isAllPermissionGranted: Boolean
        get() {
            var isNotGranted = false
            for (permission in Constant.permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    isNotGranted = true
                }
            }
            return !isNotGranted
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == Constant.REQUEST_CODE_PERMISSIONS) {
            if (isAllPermissionGranted) {
                onPermissionGranted()
            }
            else{
                showSettingsDialog()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(R.string.permission_required)
            setMessage(getString(R.string.need_permissions_to_run_app))
            setPositiveButton(R.string.go_to_setting){ _, _ ->
                openSettings()
            }
            setNegativeButton(R.string.cancel){ dialog,_ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        activityResultLauncher.launch(intent)
    }

    private val activityResultLauncher = registerForActivityResult(StartActivityForResult()) {
        if (isAllPermissionGranted) {
            onPermissionGranted()
        } else {
            showSettingsDialog()
        }
    }

    fun onPermissionGranted() {
        viewModel!!.insertAllMessageToRoom(this)
        viewModel!!.insertAllConversationsToRoom(this)
        viewModel!!.insertAllContactsToRoom(this)
        receiveMsg()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun setupData() {
    }

    private fun receiveMsg() {
        val br = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onReceive(p0: Context?, p1: Intent?) {
                val messageBody = StringBuilder()
                var address: String? = ""
                for (sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                    messageBody.append(sms.messageBody)
                    address = sms.originatingAddress
                }
                viewModel!!.message.postValue(
                    Event(
                        Message(
                            address = address.toString().replace("+84", "0")
                                .replace("\\s+".toRegex(), "").replace("-",""),
                            body = messageBody.toString(),
                            date = Calendar.getInstance().timeInMillis
                        )
                    )
                )
            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }
}