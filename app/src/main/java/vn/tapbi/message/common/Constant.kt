package vn.tapbi.message.common

import vn.tapbi.message.R

object Constant {
    const val DB_VERSION = 2
    const val DB_NAME = "AppDatabase.db"
    const val REQUEST_CODE_PERMISSIONS = 2022
    val permissions = arrayOf(
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.WRITE_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS"
    )

    const val link_share_app = "https://play.google.com/store/apps/details?id="
    const val MESSAGE_ID = "MESSAGE_ID"
    const val CONVERSATION_ID = "CONVERSATION_ID"
    const val PACKAGE_NAME = "vn.tapbi.message.ui.dialog"
    const val RATED_THE_APP = "RATED_THE_APP"
    const val ACTION_SEARCH = "action_search"
    const val DESTINATION_ID = "DESTINATION_ID"
    const val LOAD_ALL_CONVERSATION_FIRST_TIME = "load_all_conversation_first_time"
    const val LOAD_ALL_SMS_FIRST_TIME = "load_all_sms_first_time"
    const val LOAD_ALL_CONTACT_FIRST_TIME = "load_all_contact_first_time"
    const val SPLASH_DELAY_TIME = 1000L
    const val GET_CONTACT_NAME_OR_ADDRESS = 4
    const val GET_PHOTO_URI = 6
    const val GET_CONTACT_ID = 5
    const val UNCHECK_POSITION = -1
    const val DEFAULT_WALLPAPER_LANDSCAPE_VALUE = "default_wallpaper_landscape_value"
    const val DEFAULT_WALLPAPER_COLOR_VALUE = - 2
    const val DEFAULT_STAR_POSITION = -3
    const val DEFAULT_FONT = "font/sf_pro_text_regular_(default).ttf"
    const val CHAT_WALLPAPER_LANDSCAPE_SELECTED = "chat_wallpaper_landscape_selected"
    const val CHAT_WALLPAPER_COLOR_SELECTED = "chat_wallpaper_color_selected"
    const val FONT_SELECTED = "font_selected"
    const val RATED_STARS = "rated_stars"
    const val DEFAULT_RATED_STARS = 0

    val CHAT_WALLPAPER_COLORS = listOf(
        R.color.white,
        R.color.black,
        R.color.red,
        R.color.orange,
        R.color.yellow,
        R.color.green_1,
        R.color.green_2,
        R.color.green_3,
        R.color.blue_1,
        R.color.blue_2,
        R.color.blue_3,
        R.color.purple,
        R.color.blue_4
    )

    val STAR_LIST = listOf(
        R.drawable.star_2,
        R.drawable.star_2,
        R.drawable.star_2,
        R.drawable.star_2,
        R.drawable.star_2
    )
}