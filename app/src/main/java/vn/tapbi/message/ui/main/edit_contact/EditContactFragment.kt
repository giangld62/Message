package vn.tapbi.message.ui.main.edit_contact

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import timber.log.Timber
import vn.tapbi.message.R
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.databinding.FragmentEditContactBinding
import vn.tapbi.message.ui.base.BaseBindingFragment
import vn.tapbi.message.ui.main.MainActivity
import vn.tapbi.message.utils.gone
import vn.tapbi.message.utils.safeDelay
import vn.tapbi.message.utils.show
import vn.tapbi.message.utils.showToast


class EditContactFragment :
    BaseBindingFragment<FragmentEditContactBinding, EditContactViewModel>() {
    private val navArgs by navArgs<EditContactFragmentArgs>()
    private var contact: Contact? = null
    private var uri: Uri? = null
    private var isImageValid = true
    private var isPhoneNumberValid = true
    override fun getViewModel(): Class<EditContactViewModel> {
        return EditContactViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_edit_contact

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

    }


    override fun onPermissionGranted() {

    }

    override fun initView() {
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun evenClick() {
        binding.ivCancelPhoneNumber.setOnClickListener {
            hideCancelImage(binding.edtPhoneNumber,binding.ivCancelPhoneNumber)
        }
        binding.ivCancelUserName.setOnClickListener {
            hideCancelImage(binding.edtUserName,binding.ivCancelUserName)
        }

        safeDelay(10L) {
            binding.edtUserName.addTextChangedListener {
                handleTextChanged()
                if (it.isNullOrEmpty()) {
                    binding.ivCancelUserName.visibility = View.GONE
                } else {
                    binding.ivCancelUserName.visibility = View.VISIBLE
                }
            }
            binding.edtPhoneNumber.addTextChangedListener {
                handleTextChanged()
                if (it.isNullOrEmpty()) {
                    isPhoneNumberValid = false
                    binding.ivCancelPhoneNumber.visibility = View.GONE
                } else {
                    binding.ivCancelPhoneNumber.visibility = View.VISIBLE
                    safeDelay(300L) {
                        contact?.let { contact ->
                            if (binding.edtPhoneNumber.text.toString()
                                    .trim() != contact.phoneNumber
                            ) {
                                viewModel.checkPhoneNumber(contact.contactId, it.toString())
                            }
                        }
                    }
                }
            }
        }

        binding.tvCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.edtPhoneNumber.setOnFocusChangeListener { _, hasFocus ->
            handleFocusEditText(hasFocus,binding.edtPhoneNumber,binding.ivCancelPhoneNumber)
        }
        binding.edtUserName.setOnFocusChangeListener { _, hasFocus ->
            handleFocusEditText(hasFocus,binding.edtUserName,binding.ivCancelUserName)
        }

        binding.tvAddImage.setOnClickListener {
            if ((activity as MainActivity).checkTime()) {
                openGallery()
            }
        }
        binding.tvDone.setOnClickListener {
            updateUserName()
            updatePhoneNumber()
            updatePhoto()
            updateConversationAndMessages()
            if(isPhoneNumberValid){
                requireActivity().onBackPressed()
            }
        }

    }

    private fun handleFocusEditText(hasFocus: Boolean,editText: EditText,imageView: ImageView) {
        if (hasFocus && editText.text.toString().isNotEmpty())
            imageView.show()
        else
            imageView.gone()
    }

    private fun handleTextChanged() {
        binding.tvDone.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue_1
            )
        )
        binding.tvDone.isEnabled = true
    }

    private fun hideCancelImage(editText: EditText, imageView: ImageView) {
        editText.setText("")
        imageView.gone()
    }

    private fun updateConversationAndMessages() {
        contact?.let { contact ->
            if (binding.edtPhoneNumber.text.toString()
                    .trim() != contact.phoneNumber && isPhoneNumberValid
            ) {
                if (isImageValid && uri != null) {
                    updateConversationAndMessageWhenAddressChange(contact, uri.toString())
                } else {
                    updateConversationAndMessageWhenAddressChange(contact, contact.photoUri)
                }
            } else {
                if (isImageValid && uri != null) {
                    mainViewModel.updateConversationAndMessage(
                        address = contact.phoneNumber,
                        contactId = contact.contactId,
                        photoLink = uri.toString(),
                        contactName = binding.edtUserName.text.toString().trim()
                    )
                } else {
                    mainViewModel.updateConversationAndMessage(
                        address = contact.phoneNumber,
                        contactId = contact.contactId,
                        photoLink = contact.photoUri,
                        contactName = binding.edtUserName.text.toString().trim()
                    )
                }
            }
        }
    }

    private fun updateConversationAndMessageWhenAddressChange(contact: Contact, photoLink: String?) {
        mainViewModel.updateConversationAndMessage(
            address = binding.edtPhoneNumber.text.toString().trim(),
            contactId = contact.contactId,
            photoLink = photoLink,
            contactName = binding.edtUserName.text.toString().trim()
        )
        mainViewModel.updateConversationAndMessage(
            contactId = -1,
            photoLink = null,
            contactName = contact.phoneNumber,
            address = contact.phoneNumber
        )
    }

    private fun updateUserName() {
        val userName = binding.edtUserName.text.toString().trim()

        contact?.let { contact ->
            viewModel.updateName(
                contact.contactId,
                contact.rawContactId,
                userName,
                requireContext()
            )
            if (userName.isEmpty() || userName.isBlank()) {
                viewModel.updateContactNameToRoom(contact.contactId, contact.phoneNumber)
            } else {
                viewModel.updateContactNameToRoom(contact.contactId, userName)
            }
        }
    }

    private fun updatePhoneNumber() {
        val phoneNumber = binding.edtPhoneNumber.text.toString().trim()
        if (isPhoneNumberValid) {
            contact?.let { contact ->
                viewModel.updatePhoneNumber(
                    contact.contactId,
                    contact.rawContactId,
                    phoneNumber,
                    requireContext()
                )
                viewModel.updateContactPhoneNumberToRoom(
                    contact.contactId,
                    phoneNumber
                )
            }
        } else {
            showToast(requireContext(), getString(R.string.invalid_phone_number))
        }
    }

    private fun updatePhoto() {
        contact?.let { contact ->
            uri?.let { uri ->
                if (isImageValid) {
                    viewModel.writeDisplayPhoto(
                        requireContext(),
                        contact.rawContactId.toLong(),
                        uri
                    )
                    viewModel.updateContactPhotoToRoom(contact.contactId, uri.toString())
                }
            }
        }
    }


    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        mActivityResultLauncher.launch(
            Intent.createChooser(
                intent,
                getString(R.string.select_picture)
            )
        )
    }

    private val mActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                uri = it?.data?.data
                if (uri != null) {
                    Glide.with(requireContext()).load(uri)
                        .placeholder(R.drawable.default_avatar_icon).error(contact?.photoUri)
                        .listener(
                            glideCallback
                        ).into(binding.ivAvatar)
                }
            }
        }

    private val glideCallback = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            showToast(requireContext(), getString(R.string.invalid_image))
            isImageValid = false
            return true
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            binding.tvAddImage.text = getString(R.string.edit)
            binding.tvDone.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_1))
            binding.tvDone.isEnabled = true
            binding.ivAvatar.setImageDrawable(resource)
            isImageValid = true
            return true
        }
    }

    override fun observerData() {
        if (navArgs.contactId != -1) {
            mainViewModel.getContactLiveData(navArgs.contactId).observe(requireActivity()) {
                contact = it
                if (it.photoUri != null) {
                    Glide.with(binding.ivAvatar).load(it.photoUri)
                        .placeholder(R.drawable.default_avatar_icon).into(binding.ivAvatar)
                    binding.tvAddImage.text = getString(R.string.edit)
                } else {
                    binding.ivAvatar.setImageResource(R.drawable.default_avatar_icon)
                }
                binding.edtUserName.setText(it.name)
                binding.edtPhoneNumber.setText(it.phoneNumber)
            }
        }

        viewModel.isPhoneNumberExist.observe(viewLifecycleOwner) {
            if (it) {
                isPhoneNumberValid = false
                showToast(requireContext(), getString(R.string.phone_number_already_exist))
            } else {
                isPhoneNumberValid = true
            }
        }
    }

    override fun initData() {

    }
}