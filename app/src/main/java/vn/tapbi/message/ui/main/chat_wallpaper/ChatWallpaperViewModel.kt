package vn.tapbi.message.ui.main.chat_wallpaper

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.tapbi.message.data.repository.AssetsRepository
import vn.tapbi.message.data.repository.SharedPreferenceRepository
import vn.tapbi.message.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ChatWallpaperViewModel @Inject constructor(
    private val sharedPreferenceRepository: SharedPreferenceRepository,
    private val assetsRepository: AssetsRepository
) :
    BaseViewModel() {
    val landScapeImages = MutableLiveData<List<String>>()


    fun saveWallpaperColor(color: Int) {
        sharedPreferenceRepository.saveWallpaperColor(color)
    }

    fun saveWallpaperLandscape(landscapeLink: String) {
        sharedPreferenceRepository.saveWallpaperLandscape(landscapeLink)
    }

    fun getAllLandscapeImages(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            landScapeImages.postValue(assetsRepository.getAllLandscapeImages(context))
        }
    }
}