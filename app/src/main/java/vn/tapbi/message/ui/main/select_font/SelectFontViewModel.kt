package vn.tapbi.message.ui.main.select_font

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.tapbi.message.data.model.Font
import vn.tapbi.message.data.repository.AssetsRepository
import vn.tapbi.message.data.repository.SharedPreferenceRepository
import vn.tapbi.message.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SelectFontViewModel @Inject constructor(
    private val sharedPreferenceRepository: SharedPreferenceRepository,
    private val assetsRepository: AssetsRepository
) :
    BaseViewModel() {
    val fonts = MutableLiveData<List<Font>>()

    fun saveFontSelected(fontLink: String) {
        sharedPreferenceRepository.saveFontSelected(fontLink)
    }

    fun getAllFont(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            fonts.postValue(assetsRepository.getAllFont(context))
        }
    }
}