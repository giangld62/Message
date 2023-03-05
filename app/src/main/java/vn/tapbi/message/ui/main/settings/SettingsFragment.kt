package vn.tapbi.message.ui.main.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.databinding.FragmentSettingsBinding
import vn.tapbi.message.ui.base.BaseBindingFragment
import vn.tapbi.message.ui.dialog.RateAppDialogFragment
import vn.tapbi.message.ui.main.MainActivity
import vn.tapbi.message.utils.gone
import vn.tapbi.message.utils.show
import vn.tapbi.message.utils.showToast

class SettingsFragment : BaseBindingFragment<FragmentSettingsBinding,SettingsViewModel>() {

    private val mBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun getViewModel(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_settings

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mBackPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).binding?.bnvMain?.show()
        if(sharedPreferenceHelper.getBoolean(Constant.RATED_THE_APP,false)){
            binding.tvRateApp.gone()
            binding.viewDivider3.gone()
        }
    }

    override fun onPermissionGranted() {

    }

    override fun initView() {
        if(!(activity as MainActivity).isAllPermissionGranted){
            (activity as MainActivity).showSettingsDialog()
        }
    }

    override fun evenClick() {
       binding.tvChatWallpaper.setOnClickListener {
           if ((activity as MainActivity).checkTime()) {
               findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToChatWallpaperFragment())
               (activity as MainActivity).binding?.bnvMain?.gone()
           }
       }

        binding.tvFont.setOnClickListener {
            if ((activity as MainActivity).checkTime()) {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSelectFontFragment())
                (activity as MainActivity).binding?.bnvMain?.gone()
            }
        }

        binding.tvRateApp.setOnClickListener {
            if ((activity as MainActivity).checkTime()) {
                RateAppDialogFragment(object: RateAppDialogFragment.RateAppDialogListener{
                    override fun onRateAppClick(numberOfRatedStars: Int) {
                        showToast(requireContext(),getString(R.string.rated_stars))
                        sharedPreferenceHelper.storeBoolean(Constant.RATED_THE_APP,true)
                        viewModel.saveRatedStars(numberOfRatedStars)
                    }

                    override fun getRatedStars(): Int {
                        return viewModel.getRatedStars()
                    }
                }).show(requireActivity().supportFragmentManager,null)
            }
        }
    }

    override fun observerData() {

    }

    override fun initData() {

    }
}