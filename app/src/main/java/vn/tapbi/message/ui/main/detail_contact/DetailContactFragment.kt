package vn.tapbi.message.ui.main.detail_contact

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.databinding.FragmentDetailContactBinding
import vn.tapbi.message.ui.base.BaseBindingFragment

class DetailContactFragment :
    BaseBindingFragment<FragmentDetailContactBinding, DetailContactViewModel>() {
    private val navArgs by navArgs<DetailContactFragmentArgs>()

    override fun getViewModel(): Class<DetailContactViewModel> {
        return DetailContactViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_detail_contact

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

    }

    override fun onPermissionGranted() {

    }

    override fun initView() {
        binding.tvMessage.width = (Resources.getSystem().displayMetrics.widthPixels*0.395).toInt()
        binding.tvContact.width = (Resources.getSystem().displayMetrics.widthPixels*0.395).toInt()
    }


    override fun evenClick() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvEdit.setOnClickListener {
            findNavController().navigate(
                DetailContactFragmentDirections.actionDetailContactFragmentToEditContactFragment(
                    navArgs.contactId
                )
            )
        }

        binding.tvMessage.setOnClickListener {
            val phoneNumber = binding.tvPhoneNumber.text.toString()
            findNavController().navigate(
                DetailContactFragmentDirections.actionDetailContactFragmentToDetailMessageFragment(
                    contactId = navArgs.contactId,
                    address = phoneNumber
                )
            )
            sharedPreferenceHelper.storeInt(Constant.DESTINATION_ID, R.id.detailContactFragment)
        }

        binding.tvContact.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:${binding.tvPhoneNumber.text}")
            }
            startActivity(intent)
        }

    }

    override fun observerData() {
        mainViewModel.getContactLiveData(navArgs.contactId).observe(requireActivity()) {
            it?.let {
                if (it.photoUri != null) {
                    Glide.with(binding.ivAvatar).load(it.photoUri)
                        .placeholder(R.drawable.default_avatar_icon).into(binding.ivAvatar)
                }
                binding.tvUserName.text = it.name
                binding.tvPhoneNumber.text = it.phoneNumber
            }
        }
    }

    override fun initData() {
    }
}