package vn.tapbi.message.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant.SPLASH_DELAY_TIME
import vn.tapbi.message.ui.main.MainActivity
import vn.tapbi.message.utils.gone
import vn.tapbi.message.utils.safeDelay


class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).binding?.bnvMain?.gone()
        val window: Window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.purple_2)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeDelay(SPLASH_DELAY_TIME) {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToConversationsFragment())
        }
    }
}