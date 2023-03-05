package vn.tapbi.message.ui.main.edit_contact

import android.content.Context
import android.net.Uri
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
class EditContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) :
    BaseViewModel() {
    val isPhoneNumberExist = MutableLiveData<Boolean>()

    fun updateContactNameToRoom(contactId: Int, contactName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.updateContactName(contactId, contactName)
        }
    }

    fun updateContactPhoneNumberToRoom(contactId: Int, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.updateContactPhoneNumber(contactId, phoneNumber)
        }
    }

    fun updateContactPhotoToRoom(contactId: Int, photoString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.updateContactPhoto(contactId, photoString)
        }
    }

    fun updateName(contactId: Int, rawContactId: Int, name: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.updateName(contactId, rawContactId, name, context)
        }
    }

    fun updatePhoneNumber(
        contactId: Int,
        rawContactId: Int,
        phoneNumber: String,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.updatePhoneNumber(contactId, rawContactId, phoneNumber, context)
        }
    }

    fun writeDisplayPhoto(context: Context, id: Long, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.writeDisplayPhoto(context, id, uri)
        }
    }

    fun checkPhoneNumber(contactId: Int, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isPhoneNumberExist.postValue(
                contactRepository.isPhoneNumberExist(
                    contactId,
                    phoneNumber
                )
            )
        }
    }

}