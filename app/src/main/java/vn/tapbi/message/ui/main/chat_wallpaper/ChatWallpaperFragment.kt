package vn.tapbi.message.ui.main.chat_wallpaper

import android.os.Bundle
import android.view.View
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.common.Constant.CHAT_WALLPAPER_COLORS
import vn.tapbi.message.common.Constant.DEFAULT_WALLPAPER_COLOR_VALUE
import vn.tapbi.message.common.Constant.DEFAULT_WALLPAPER_LANDSCAPE_VALUE
import vn.tapbi.message.databinding.FragmentChatWallpaperBinding
import vn.tapbi.message.ui.adapter.ChatWallpaperColorAdapter
import vn.tapbi.message.ui.adapter.ChatWallpaperLandscapeAdapter
import vn.tapbi.message.ui.base.BaseBindingFragment
import vn.tapbi.message.ui.main.MainActivity
import vn.tapbi.message.utils.safeDelay

class ChatWallpaperFragment : BaseBindingFragment<FragmentChatWallpaperBinding,ChatWallpaperViewModel>() {
    private lateinit var chatWallpaperColorAdapter: ChatWallpaperColorAdapter
    private lateinit var chatWallpaperLandscapeAdapter: ChatWallpaperLandscapeAdapter

    override fun getViewModel(): Class<ChatWallpaperViewModel> {
        return ChatWallpaperViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_chat_wallpaper

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

    }

    override fun onPermissionGranted() {
    }

    override fun initView() {
        chatWallpaperColorAdapter = ChatWallpaperColorAdapter(CHAT_WALLPAPER_COLORS,requireContext())
        binding.rvChatWallpaperColor.adapter = chatWallpaperColorAdapter
        binding.rvChatWallpaperColor.setHasFixedSize(true)

        chatWallpaperLandscapeAdapter = ChatWallpaperLandscapeAdapter()
        binding.rvChatWallpaperLandscape.adapter = chatWallpaperLandscapeAdapter
        binding.rvChatWallpaperLandscape.setHasFixedSize(true)

        if (mainViewModel.getWallPaperColorSelected() != DEFAULT_WALLPAPER_COLOR_VALUE) {
            chatWallpaperColorAdapter.setCheckedColor(mainViewModel.getWallPaperColorSelected())
        }

        if (mainViewModel.getWallPaperLandscapedSelected() != DEFAULT_WALLPAPER_LANDSCAPE_VALUE) {
            safeDelay(50L){
                chatWallpaperLandscapeAdapter.setCheckedLandscape(mainViewModel.getWallPaperLandscapedSelected())
            }
        }
    }

    override fun evenClick() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        chatWallpaperColorAdapter.setOnItemClickListener {
            if ((activity as MainActivity).checkTime()) {
                chatWallpaperLandscapeAdapter.unCheck()
                viewModel.saveWallpaperColor(it)
                viewModel.saveWallpaperLandscape(DEFAULT_WALLPAPER_LANDSCAPE_VALUE)
            }
        }

        chatWallpaperLandscapeAdapter.setOnItemClickListener {
            if ((activity as MainActivity).checkTime()) {
                chatWallpaperColorAdapter.unCheck()
                viewModel.saveWallpaperLandscape(it)
                viewModel.saveWallpaperColor(DEFAULT_WALLPAPER_COLOR_VALUE)
            }
        }

    }

    override fun observerData() {
        viewModel.landScapeImages.observe(viewLifecycleOwner){
            chatWallpaperLandscapeAdapter.differ.submitList(it)
        }
    }

    override fun initData() {
        viewModel.getAllLandscapeImages(requireActivity())
    }
}