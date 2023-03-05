package vn.tapbi.message.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import vn.tapbi.message.ui.main.MainViewModel

abstract class BaseBindingFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseFragment() {
    lateinit var binding: B
    lateinit var viewModel: T
    lateinit var mainViewModel: MainViewModel
    protected abstract fun getViewModel(): Class<T>
    abstract val layoutId: Int

    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    protected abstract fun onPermissionGranted()
    protected abstract fun initView()
    protected abstract fun evenClick()
    protected abstract fun observerData()
    protected abstract fun initData()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(getViewModel())
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        onCreatedView(view, savedInstanceState)
        onPermissionGranted()
        initView()
        observerData()
        evenClick()
        initData()
    }
}