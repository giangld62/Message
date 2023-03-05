package vn.tapbi.message.ui.main.contact

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.data.repository.ContactRepository
import vn.tapbi.message.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : BaseViewModel() {
    val contactsCount = MutableLiveData<Int>()
    val contactsCountFromContentProvider = MutableLiveData<Int>()

    fun getAllContacts():LiveData<List<Contact>>{
       return contactRepository.getAllContactsLiveData();
    }

    fun getContactsCount() {
        viewModelScope.launch(Dispatchers.IO) {
            contactsCount.postValue(contactRepository.getContactsCount())
        }
    }

    fun getContactsCountFromContentProvider(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            contactsCountFromContentProvider.postValue(
                contactRepository.getContactsCountFromContentProvider(
                    context
                )
            )
        }
    }

    fun deleteContactsFromRoom(context: Context, numberOfContactsToDelete: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.deleteContacts(context, numberOfContactsToDelete)
        }
    }
}