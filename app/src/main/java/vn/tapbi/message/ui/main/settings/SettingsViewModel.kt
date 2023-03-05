package vn.tapbi.message.ui.main.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import vn.tapbi.message.data.repository.SharedPreferenceRepository
import vn.tapbi.message.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val sharedPreferenceRepository: SharedPreferenceRepository) :
    BaseViewModel() {

    fun getRatedStars(): Int {
        return sharedPreferenceRepository.getRatedStars()
    }

    fun saveRatedStars(star: Int) {
        sharedPreferenceRepository.saveRatedStars(star)
    }
}