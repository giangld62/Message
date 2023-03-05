package vn.tapbi.message.ui.main.select_font

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import vn.tapbi.message.R
import vn.tapbi.message.databinding.FragmentSelectFontBinding
import vn.tapbi.message.ui.adapter.FontAdapter
import vn.tapbi.message.ui.base.BaseBindingFragment
import vn.tapbi.message.ui.main.MainActivity
import vn.tapbi.message.utils.HorizontalDividerDecoration
import vn.tapbi.message.utils.safeDelay


class SelectFontFragment : BaseBindingFragment<FragmentSelectFontBinding, SelectFontViewModel>() {
    private var fontAdapter: FontAdapter? = null

    override fun getViewModel(): Class<SelectFontViewModel> {
        return SelectFontViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_select_font

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fontAdapter = null
    }

    override fun onPermissionGranted() {

    }

    override fun initView() {
        fontAdapter = FontAdapter()
        binding.rvFont.adapter = fontAdapter
        binding.rvFont.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFont.addItemDecoration(
            HorizontalDividerDecoration.Builder(requireContext())
                .color(requireContext().resources.getColor(R.color.gray_2))
                .sizeResId(R.dimen.divider_pb)
                .build()
        )

        val fontResourceSelected = mainViewModel.getFontSelected()
        if (fontResourceSelected != null) {
            safeDelay(50L){
                fontAdapter?.setCheckedFont(fontResourceSelected)
            }
        }
    }

    override fun evenClick() {
        fontAdapter?.setOnItemClickListener {
            viewModel.saveFontSelected(it)
        }

        binding.ivBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    override fun observerData() {
        viewModel.fonts.observe(viewLifecycleOwner){
            fontAdapter?.submitList(it)
        }
    }

    override fun initData() {
        viewModel.getAllFont(requireActivity())
    }
}