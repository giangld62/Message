package vn.tapbi.message.data.repository

import android.app.Activity
import android.content.Context
import vn.tapbi.message.data.model.Font
import javax.inject.Inject

class AssetsRepository @Inject constructor() {

    fun getAllLandscapeImages(context: Context): List<String>{
        val imagesLink = arrayListOf<String>()
        val f = context.assets.list("LandscapeImages")
        f?.let {
            for( file in it){
                imagesLink.add("file:///android_asset/LandscapeImages/$file")
            }
        }
        return imagesLink
    }

    fun getAllFont(context: Context): List<Font>{
        val fonts = arrayListOf<Font>()
        val f = context.assets.list("font")
        f?.let {
            for( file in it){
                fonts.add(Font(file.substring(0,file.length-4).replace("_"," "),"font/$file"))
            }
        }
        return fonts
    }
}