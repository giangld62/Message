package vn.tapbi.message.ui.main.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import timber.log.Timber
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.data.model.SearchResult
import vn.tapbi.message.databinding.FragmentConversationsBinding
import vn.tapbi.message.ui.adapter.ConversationAdapter
import vn.tapbi.message.ui.adapter.SearchResultAdapter
import vn.tapbi.message.ui.base.BaseBindingFragment
import vn.tapbi.message.ui.main.MainActivity
import vn.tapbi.message.utils.gone
import vn.tapbi.message.utils.safeDelay
import vn.tapbi.message.utils.show


class ConversationsFragment :
    BaseBindingFragment<FragmentConversationsBinding, ConversationViewModel>() {
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var searchResultAdapter: SearchResultAdapter
    private var messageCountFromContentProvider = 0

    override fun getViewModel(): Class<ConversationViewModel> {
        return ConversationViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_conversations

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            mBackPressedCallback
        )
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun initView() {
        safeDelay(300L) { requireActivity().window.statusBarColor = Color.WHITE }
        conversationAdapter = ConversationAdapter()
        searchResultAdapter = SearchResultAdapter(object : SearchResultAdapter.OnItemClick {
            override fun onItemClick(searchResult: SearchResult) {
                if ((activity as MainActivity).checkTime()) {
                    sharedPreferenceHelper.storeInt(
                        Constant.DESTINATION_ID,
                        R.id.conversationsFragment
                    )
                    findNavController().navigate(
                        ConversationsFragmentDirections.actionConversationsFragmentToDetailMessageFragment(
                            threadId = searchResult.threadId,
                            address = searchResult.address
                        )
                    )
                    (activity as MainActivity).binding?.bnvMain?.gone()
                }
            }
        })
        binding.rvConversations.adapter = conversationAdapter
        binding.rvConversations.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun evenClick() {
        conversationAdapter.setOnItemClickListener {
            Timber.d("giangledinh ${it.address}")
            if ((activity as MainActivity).checkTime()) {
                sharedPreferenceHelper.storeInt(Constant.DESTINATION_ID, R.id.conversationsFragment)
                if (it.read != 1) {
                    mainViewModel.markConversationAsRead(it.threadId)
                }
                findNavController().navigate(
                    ConversationsFragmentDirections.actionConversationsFragmentToDetailMessageFragment(
                        contactId = it.contactId!!,
                        threadId = it.threadId,
                        contactName = it.contactName,
                        address = it.address,
                        conversationId = it.id
                    )
                )
                (activity as MainActivity).binding?.bnvMain?.gone()
            }
        }

        binding.ivNewMessage.setOnClickListener {
            if ((activity as MainActivity).checkTime()) {
                findNavController().navigate(ConversationsFragmentDirections.actionConversationsFragmentToNewMessageFragment())
                (activity as MainActivity).binding?.bnvMain?.gone()
            }
        }
        binding.edtSearch.addTextChangedListener { editable ->
            editable?.let {
                if (editable.toString().isNotEmpty() && editable.toString().isNotBlank()) {
                    binding.ivCancel.show()
                    if (sharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_SMS_FIRST_TIME, true)) {
                        binding.rvConversations.adapter = conversationAdapter
                        viewModel.searchForConversations("%${editable.toString().trim()}%")
                    } else {
                        binding.rvConversations.adapter = searchResultAdapter
                        viewModel.searchForMessages("%${editable.toString().trim()}%")
                    }
                } else {
                    binding.ivCancel.gone()
                    binding.rvConversations.adapter = conversationAdapter
                    viewModel.searchForConversations("")
                }
            }

        }

        binding.ivCancel.setOnClickListener {
            binding.edtSearch.setText("")
        }

//        binding.edtSearch.setOnFocusChangeListener { view, b ->
//            if (binding.edtSearch.text.isEmpty() || binding.edtSearch.text.isBlank()) {
//                if (!b) {
//                    mainViewModel.getAllConversations(requireContext())
//                } else {
//                    conversationAdapter.submitList(null)
//                }
//            }
//        }
    }

    private val mBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }


    override fun observerData() {
        viewModel.conversationsLiveData.observe(requireActivity()) {
            conversationAdapter.submitList(it)
        }

        mainViewModel.messageCountFromContentProvider.observe(requireActivity()) { event ->
            event.getContentIfNotHandled()?.let {
                Timber.d("messageCountFromRoom ${it}")
                messageCountFromContentProvider = it
                mainViewModel.getMessageCountFromRoom()
            }
        }

        mainViewModel.messageCountFromRoom.observe(requireActivity()) { event ->
            event.getContentIfNotHandled()?.let { messageCountFromRoom ->
                Timber.d("messageCountFromRoom ${messageCountFromRoom} - ${messageCountFromContentProvider}")
                if (messageCountFromRoom < messageCountFromContentProvider && messageCountFromRoom != 0) {
                    mainViewModel.insertListMessage(requireContext(), messageCountFromRoom)
                }
            }
        }


        mainViewModel.message.observe(requireActivity()) { event ->
            event.getContentIfNotHandled()?.let {
                safeDelay(300L) {
                    mainViewModel.getMessageCountFromContentProvider(requireContext())
                }
            }
        }

        viewModel.searchMessages.observe(viewLifecycleOwner) {
            searchResultAdapter.submitList(it)
        }

        mainViewModel.insertAllConversations.observe(requireActivity()) {
            binding.pbLoad.gone()
        }

        viewModel.searchForConversation.observe(viewLifecycleOwner) {
            conversationAdapter.submitList(it)
        }

    }

    override fun initData() {
    }

    override fun onResume() {
        if (sharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_CONVERSATION_FIRST_TIME, true))
            binding.pbLoad.show()
        super.onResume()
        safeDelay(300L) {
            (activity as MainActivity).binding?.bnvMain?.show()
        }
        if ((activity as MainActivity).isAllPermissionGranted) {
            mainViewModel.getMessageCountFromContentProvider(requireContext())
        } else {
            (activity as MainActivity).showSettingsDialog()
            binding.pbLoad.gone()
        }

    }

    override fun onPermissionGranted() {
    }

}