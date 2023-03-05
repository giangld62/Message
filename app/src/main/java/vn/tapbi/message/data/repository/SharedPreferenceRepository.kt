package vn.tapbi.message.data.repository

import vn.tapbi.message.common.Constant
import vn.tapbi.message.common.Constant.DEFAULT_FONT
import vn.tapbi.message.common.Constant.FONT_SELECTED
import vn.tapbi.message.data.local.SharedPreferenceHelper
import javax.inject.Inject

class SharedPreferenceRepository @Inject constructor(private val sharedPreferenceHelper: SharedPreferenceHelper) {

    fun getWallpaperColorSelected(): Int{
        return sharedPreferenceHelper.getIntWithDefault(
            Constant.CHAT_WALLPAPER_COLOR_SELECTED,
            Constant.DEFAULT_WALLPAPER_COLOR_VALUE
        )
    }

    fun getWallpaperLandscapedSelected() : String?{
        return sharedPreferenceHelper.getStringWithDefault(
            Constant.CHAT_WALLPAPER_LANDSCAPE_SELECTED,
            Constant.DEFAULT_WALLPAPER_LANDSCAPE_VALUE
        )
    }

    fun saveWallpaperColor(color: Int){
        sharedPreferenceHelper.storeInt(Constant.CHAT_WALLPAPER_COLOR_SELECTED,color)
    }

    fun saveWallpaperLandscape(landscapeLink: String){
        sharedPreferenceHelper.storeString(Constant.CHAT_WALLPAPER_LANDSCAPE_SELECTED,landscapeLink)
    }

    fun saveFontSelected(fontLink: String){
        sharedPreferenceHelper.storeString(FONT_SELECTED,fontLink)
    }

    fun getFontSelected(): String?{
        return sharedPreferenceHelper.getStringWithDefault(FONT_SELECTED, DEFAULT_FONT)
    }

    fun saveRatedStars(star: Int){
        sharedPreferenceHelper.storeInt(Constant.RATED_STARS,star)
    }

    fun getRatedStars(): Int{
        return sharedPreferenceHelper.getIntWithDefault(Constant.RATED_STARS,Constant.DEFAULT_RATED_STARS)
    }
}