package vn.tapbi.message.ui.base

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import vn.tapbi.message.data.local.SharedPreferenceHelper
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment(){
    @Inject lateinit var sharedPreferenceHelper: SharedPreferenceHelper
}