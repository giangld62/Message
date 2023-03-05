package vn.tapbi.message.ui.main.new_message

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.databinding.FragmentNewMessageBinding
import vn.tapbi.message.ui.adapter.ContactAdapter
import vn.tapbi.message.ui.base.BaseBindingFragment
import vn.tapbi.message.utils.*


class NewMessageFragment : BaseBindingFragment<FragmentNewMessageBinding, NewMessageViewModel>() {
    private lateinit var contactAdapter: ContactAdapter
    private var contact: Contact? = null
    private var messageContent: String = ""
    private var address: String = ""

    override fun getViewModel(): Class<NewMessageViewModel> {
        return NewMessageViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_new_message

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {}

    override fun initView() {
        contactAdapter = ContactAdapter()
        binding.rvContacts.adapter = contactAdapter
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.tvRecipient.maxWidth =
            (Resources.getSystem().displayMetrics.widthPixels / 1.5).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun evenClick() {
        contactAdapter.setOnItemClickListener {
            contact = it
            binding.tvRecipient.show()
            binding.tvRecipient.text = it.name
            binding.edtContactSearch.setText("")
            binding.rvContacts.gone()
            binding.edtContactSearch.hideKeyboard()
        }

        binding.tvRecipient.setOnClickListener {
            binding.tvRecipient.gone()
            contact = null
        }

        binding.ivSend.setOnClickListener {
            if (contact == null) {
                try {
                    binding.edtContactSearch.text.toString().trim().toInt()
                } catch (e: NumberFormatException) {
                    showToast(requireContext(), getString(R.string.invalid_phone_number))
                    return@setOnClickListener
                }
                sendMessage(binding.edtContactSearch.text.toString().trim())
            } else {
                sendMessage(contact!!.phoneNumber)
            }
        }

        binding.edtContactSearch.addTextChangedListener { editable ->
            editable?.let {
                if (editable.toString().isNotEmpty() && editable.toString().isNotBlank()) {
                    binding.ivCancel.show()
                    binding.rvContacts.show()
                    mainViewModel.searchForContacts("%${editable.toString().trim()}%")
                } else {
                    binding.rvContacts.gone()
                    binding.ivCancel.gone()
                }
            }

        }

        binding.ivCancel.setOnClickListener {
            binding.edtContactSearch.setText("")
        }

        binding.tvCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendMessage(address: String) {
        this.address = address
        messageContent = binding.edtNewMessage.text.toString()
        if (messageContent.isBlank() || messageContent.isEmpty() || address.isEmpty() || address.isBlank()
        ) {
            showToast(requireContext(), getString(R.string.you_have_not_entered_content_yet))
        } else {
            if (getSimState(requireContext()) != 5 || isAirplaneModeOn(requireContext())) {
                showToast(requireContext(), getString(R.string.message_sent_failed))
                sharedPreferenceHelper.storeInt(
                    Constant.DESTINATION_ID,
                    R.id.conversationsFragment
                )
//                mainViewModel.getContactByAddress(address)
//                mainViewModel.getMaxThreadIdInRoom()
//                mainViewModel.getConversationByAddress(address)
                findNavController().navigate(
                    NewMessageFragmentDirections.actionNewMessageFragmentToDetailMessageFragment(
                        address = address,
                        contactName = if (contact != null) contact!!.name else address,
                        messageContent = messageContent
                    )
                )
            } else {
                mainViewModel.sendSMS(address, messageContent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun observerData() {
        mainViewModel.isSendMessageSuccess.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    showToast(requireContext(), getString(R.string.message_sent_successfully))
                    sharedPreferenceHelper.storeInt(
                        Constant.DESTINATION_ID,
                        R.id.conversationsFragment
                    )
                    safeDelay(300L) {
                        mainViewModel.getMessageCountFromContentProvider(
                            requireContext()
                        )
                    }
                    findNavController().navigate(
                        NewMessageFragmentDirections.actionNewMessageFragmentToDetailMessageFragment(
                            address = address, contactName = address
                        )
                    )

                } else {
                    showToast(requireContext(), getString(R.string.message_sent_failed))
                }
            }
        }

        mainViewModel.searchForContacts.observe(viewLifecycleOwner) {
            contactAdapter.submitList(it)
        }
    }

    override fun initData() {

    }

    override fun onPermissionGranted() {
    }


}