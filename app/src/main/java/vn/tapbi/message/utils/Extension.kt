package vn.tapbi.message.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.format.DateUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*

fun isAirplaneModeOn(context: Context): Boolean {
    return Settings.Global.getInt(
        context.contentResolver,
        Settings.Global.AIRPLANE_MODE_ON, 0
    ) != 0
}

@RequiresApi(Build.VERSION_CODES.N)
fun formatDate(dateLong: Long): String {
    val date = Date(dateLong)
    val isToday = DateUtils.isToday(dateLong)
    val simpleDateFormat = if (!isToday) SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    ) else SimpleDateFormat("HH:mm", Locale.getDefault())
    return if (isToday) "Today ${simpleDateFormat.format(date)}" else simpleDateFormat.format(
        date
    )
}

fun showToast(context: Context, content: String){
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
}

fun getSimState(context: Context): Int{
        return (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState
}