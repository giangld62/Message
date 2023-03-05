package vn.tapbi.message.ui.main.contact

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import timber.log.Timber
import vn.tapbi.message.R
import vn.tapbi.message.databinding.FragmentContactsBinding
import vn.tapbi.message.ui.adapter.ContactAdapter2
import vn.tapbi.message.ui.base.BaseBindingFragment
import vn.tapbi.message.ui.main.MainActivity
import vn.tapbi.message.utils.gone
import vn.tapbi.message.utils.show


class ContactsFragment : BaseBindingFragment<FragmentContactsBinding, ContactViewModel>() {
    private lateinit var contactAdapter2: ContactAdapter2
    private var isSearching = false
    private var contactCountFromContentProvider = 0

    private val mBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun getViewModel(): Class<ContactViewModel> {
        return ContactViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_contacts

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            mBackPressedCallback
        )
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).binding?.bnvMain?.show()
        viewModel.getContactsCount()
        if ((activity as MainActivity).isAllPermissionGranted) {
            viewModel.getContactsCountFromContentProvider(requireContext())
            mainViewModel.updateContactChangeFromContentProviderToRoom(requireContext())
        }
    }

    override fun onPermissionGranted() {

    }

    override fun initView() {
        contactAdapter2 = ContactAdapter2()
        binding.rvContacts.adapter = contactAdapter2
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        if (!(activity as MainActivity).isAllPermissionGranted) {
            (activity as MainActivity).showSettingsDialog()
        }
    }

    override fun evenClick() {
        contactAdapter2.setOnItemClickListener {
            if ((activity as MainActivity).checkTime()) {
                findNavController().navigate(
                    ContactsFragmentDirections.actionContactsFragmentToDetailContactFragment(it)
                )
                (activity as MainActivity).binding?.bnvMain?.gone()
            }
        }

        binding.edtSearch.addTextChangedListener { editable ->
            isSearching = true
            mainViewModel.searchForContacts("%${editable.toString().trim()}%")
        }
    }

    override fun observerData() {
        mainViewModel.insertAllContacts.observe(requireActivity()) {
            binding.pbLoad.gone()
        }

        viewModel.getAllContacts().observe(viewLifecycleOwner) {
            contactAdapter2.submitList(it)
        }

        viewModel.contactsCountFromContentProvider.observe(viewLifecycleOwner) {
            contactCountFromContentProvider = it
            viewModel.getContactsCount()
        }

        viewModel.contactsCount.observe(viewLifecycleOwner){ contactsCountFromRoom ->
            Timber.d("contactsCountFromRoom ${contactsCountFromRoom} - $contactCountFromContentProvider")
            if (contactsCountFromRoom > contactCountFromContentProvider && contactCountFromContentProvider!= 0) {
                viewModel.deleteContactsFromRoom(
                    requireContext(),
                    contactsCountFromRoom - contactCountFromContentProvider
                )
            }
        }

        mainViewModel.searchForContacts.observe(viewLifecycleOwner) {
            contactAdapter2.submitList(it)
        }
    }

    override fun initData() {
    }
}