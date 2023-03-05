package vn.tapbi.message.ui.main.detail_message

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import timber.log.Timber
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.common.Constant.DEFAULT_WALLPAPER_COLOR_VALUE
import vn.tapbi.message.common.Constant.DEFAULT_WALLPAPER_LANDSCAPE_VALUE
import vn.tapbi.message.data.local.SharedPreferenceHelper
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.data.model.Conversation
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.databinding.FragmentDetailMessageBinding
import vn.tapbi.message.ui.adapter.DetailMessageAdapter
import vn.tapbi.message.ui.base.BaseBindingFragment
import vn.tapbi.message.utils.*


class DetailMessageFragment :
    BaseBindingFragment<FragmentDetailMessageBinding, DetailMessageViewModel>() {
    private val navArgs by navArgs<DetailMessageFragmentArgs>()
    private var messages = listOf<Message>()
    private var address: String = ""
    private var messageContent: String = ""
    private var contact: Contact? = null
    private var conversationId = -1
    private var maxThreadId: Long = -1
    private var isResendErrorMessage = false
    private var messageId: Long = 0

    private lateinit var detailMessageAdapter: DetailMessageAdapter
    private var destinationId = 0

    private val mBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack(destinationId, false)
            if (messages.isNotEmpty()) {
                mainViewModel.markConversationAsRead(messages[0].threadId)
                for (item in messages) {
                    if (item.read != 1) {
                        mainViewModel.markMessageAsRead(item.id)
                    }
                }
            }
        }
    }

    override fun getViewModel(): Class<DetailMessageViewModel> {
        return DetailMessageViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_detail_message

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val contactFragmentId = sharedPreferenceHelper.getIntWithDefault(
            Constant.DESTINATION_ID,
            SharedPreferenceHelper.DEFAULT_NUM
        )
        destinationId = if (contactFragmentId == R.id.detailContactFragment) {
            R.id.detailContactFragment
        } else {
            R.id.conversationsFragment
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            mBackPressedCallback
        )
    }

    override fun initView() {
        detailMessageAdapter = DetailMessageAdapter(requireContext())
        binding.rvDetailMessage.adapter = detailMessageAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.rvDetailMessage.layoutManager = layoutManager

        val chatWallpaperColorSelected = mainViewModel.getWallPaperColorSelected()
        val chatWallpaperLandscapeSelected = mainViewModel.getWallPaperLandscapedSelected()

        if (chatWallpaperColorSelected != DEFAULT_WALLPAPER_COLOR_VALUE) {
            binding.ivBackground.setImageResource(chatWallpaperColorSelected)
        } else if (chatWallpaperLandscapeSelected != DEFAULT_WALLPAPER_LANDSCAPE_VALUE) {
            Glide.with(binding.ivBackground)
                .load(mainViewModel.getWallPaperLandscapedSelected())
                .into(binding.ivBackground)
        }

        val fontSelected = mainViewModel.getFontSelected()
        if (fontSelected != null) {
            detailMessageAdapter.setFont(fontSelected)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun evenClick() {
        binding.tvBack.setOnClickListener {
            findNavController().popBackStack(destinationId, false)
            if (messages.isNotEmpty()) {
                mainViewModel.markConversationAsRead(messages[0].threadId)
                for (item in messages) {
                    if (item.read != 1) {
                        mainViewModel.markMessageAsRead(item.id)
                    }
                }
            }
        }

        binding.ivSend.setOnClickListener {
            sendMessage()
        }

        detailMessageAdapter.setOnItemClickListener {
            if (getSimState(requireContext()) != 5 || isAirplaneModeOn(requireContext())) {
                showToast(requireContext(), getString(R.string.message_sent_failed))
            }
            else{
                mainViewModel.sendSMS(it.address!!,it.body)
                isResendErrorMessage = true
                messageId = it.id
            }
        }
    }


    private fun sendMessage() {
        address = if (messages.isNotEmpty()) messages[0].address!! else navArgs.address!!
        messageContent = binding.edtNewMessage.text.toString().trim()

        if (address.isNotEmpty() && messageContent.isNotEmpty() && messageContent.isNotBlank()) {
            if (getSimState(requireContext()) != 5 || isAirplaneModeOn(requireContext())) {
                showToast(requireContext(), getString(R.string.message_sent_failed))
                saveErrorMessageToRoom(address, messageContent)
                binding.edtNewMessage.setText("")
            } else {
                mainViewModel.sendSMS(address, messageContent)
            }
        } else {
            showToast(requireContext(), getString(R.string.you_have_not_entered_content_yet))
        }
    }

    private fun saveErrorMessageToRoom(address: String, messageContent: String) {
        mainViewModel.insertMessage(
            Message(
                date = Calendar.getInstance().timeInMillis,
                threadId = if (messages.isNotEmpty()) messages[0].threadId else maxThreadId + 1,
                body = messageContent,
                read = 1,
                photoLink = if (messages.isNotEmpty()) messages[0].photoLink else if (contact != null) contact?.photoUri else null,
                contactName = if (messages.isNotEmpty()) messages[0].contactName else if (contact != null) contact?.name else address,
                contactId = if (contact != null) contact?.contactId else -1,
                type = 5,
                address = address
            )
        )

            if (navArgs.conversationId != -1) {
                upsertConversation(navArgs.conversationId,messageContent,address)
            } else if (conversationId != -1) {
                upsertConversation(conversationId,messageContent,address)
            } else {
                upsertConversation(null,messageContent,address)

        }
    }

    private fun upsertConversation(id: Int?,messageContent: String, address: String) {
        if(id != null){
            mainViewModel.upsertConversation(
                Conversation(
                    id = id,
                    threadId = if (messages.isNotEmpty()) messages[0].threadId else maxThreadId + 1,
                    date = Calendar.getInstance().timeInMillis,
                    snippet = messageContent,
                    read = 1,
                    photoLink = if (messages.isNotEmpty()) messages[0].photoLink else if (contact != null) contact?.photoUri else null,
                    contactName = if (messages.isNotEmpty()) messages[0].contactName else if (contact != null) contact?.name else address,
                    contactId = if (contact != null) contact?.contactId else -1,
                    address = address,
                    isSentSuccessfully = false
                )
            )
        }
        else{
            mainViewModel.upsertConversation(
                Conversation(
                    threadId = if (messages.isNotEmpty()) messages[0].threadId else maxThreadId + 1,
                    date = Calendar.getInstance().timeInMillis,
                    snippet = messageContent,
                    read = 1,
                    photoLink = if (messages.isNotEmpty()) messages[0].photoLink else if (contact != null) contact?.photoUri else null,
                    contactName = if (messages.isNotEmpty()) messages[0].contactName else if (contact != null) contact?.name else address,
                    contactId = if (contact != null) contact?.contactId else -1,
                    address = address,
                    isSentSuccessfully = false
                )
            )
            mainViewModel.getConversationByAddress(navArgs.address!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun observerData() {
        if (!sharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_SMS_FIRST_TIME, true)) {
            viewModel.getMessagesOfTheConversationLiveData(navArgs.threadId, navArgs.address)
                .observe(requireActivity()) {
                    binding.rvDetailMessage.smoothScrollToPosition(detailMessageAdapter.currentList.size)
                    it?.let {
                        loadMessage(it)
                    }
                }
        } else {
            viewModel.messages.observe(requireActivity()) {
                it?.let {
                    loadMessage(it)
                }
            }
        }

        mainViewModel.getContactLiveData(navArgs.contactId).observe(requireActivity()) {
            it?.let {
                Glide.with(binding.ivAvatar).load(it.photoUri)
                    .placeholder(R.drawable.default_avatar_icon)
                    .into(binding.ivAvatar)
                binding.tvUserName.text = it.name
            }
        }

        mainViewModel.isSendMessageSuccess.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.edtNewMessage.setText("")
                    safeDelay(300L) {
                        if(isResendErrorMessage){
                            mainViewModel.updateErrorMessage(requireContext(),messageId)
                        }
                        else{
                            mainViewModel.getMessageCountFromContentProvider(requireContext())
                        }
                    }
                    showToast(requireContext(), getString(R.string.message_sent_successfully))
                } else {
                    showToast(requireContext(), getString(R.string.message_sent_failed))
                }
            }
        }

        mainViewModel.getContactByAddress.observe(requireActivity()) {
            Timber.d("getConversationByAddress contact ${it}")
            contact = it
        }

        mainViewModel.getConversationByAddress.observe(requireActivity()) {
            Timber.d("getConversationByAddress conversationId ${it == null}")
            if(it == null){
                conversationId = -1
            }
            else {
                conversationId = it.id
                Timber.d("getConversationByAddress conversationId ${conversationId}")
            }

        }

        mainViewModel.maxThreadIdInRom.observe(requireActivity()) {
            Timber.d("getConversationByAddress maxThreadId ${it}")
            maxThreadId = it
        }
    }

    private fun loadMessage(messages: List<Message>) {
        binding.pbLoad.gone()
        detailMessageAdapter.submitList(messages)
        if (messages.isNotEmpty()) {
            this.messages = messages
            if (messages.isNotEmpty()) {
                if (messages[0].photoLink == null) {
                    binding.ivAvatar.setImageResource(R.drawable.default_avatar_icon)
                } else {
                    Glide.with(binding.ivAvatar).load(messages[0].photoLink)
                        .placeholder(R.drawable.default_avatar_icon)
                        .into(binding.ivAvatar)
                }
                binding.tvUserName.text = messages[0].contactName
            }
        }
    }

    override fun initData() {
        navArgs.contactName?.let {
            binding.tvUserName.text = it
        }
        if (sharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_SMS_FIRST_TIME, true)) {
            viewModel.getMessagesOfTheConversationFromContentProvider(
                requireContext(),
                navArgs.threadId
            )
        }

        Timber.d("getContactByAddress ${messages.isEmpty()}")
        if (messages.isEmpty()) {
            Timber.d("getContactByAddress")
            mainViewModel.getContactByAddress(navArgs.address!!)
        }

        if (navArgs.conversationId == -1) {
            Timber.d("conversationId")
            mainViewModel.getConversationByAddress(navArgs.address!!)
        }

        if (navArgs.messageContent != "") {
            safeDelay(300L) {
                saveErrorMessageToRoom(navArgs.address!!, navArgs.messageContent)
            }
        }

        mainViewModel.getMaxThreadIdInRoom()
    }

    override fun onPermissionGranted() {

    }

}