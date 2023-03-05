package vn.tapbi.message.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.data.model.Conversation
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.data.repository.ContactRepository
import vn.tapbi.message.data.repository.ConversationRepository
import vn.tapbi.message.data.repository.SharedPreferenceRepository
import vn.tapbi.message.data.repository.SmsRepository
import vn.tapbi.message.ui.base.BaseViewModel
import vn.tapbi.message.utils.Event
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val smsRepository: SmsRepository,
    private val sharedPreferenceRepository: SharedPreferenceRepository,
    private val contactRepository: ContactRepository
) : BaseViewModel() {

    val message = MutableLiveData<Event<Message>>()
    val isSendMessageSuccess = MutableLiveData<Event<Boolean>>()
    val messageCountFromContentProvider = MutableLiveData<Event<Int>>()
    val messageCountFromRoom = MutableLiveData<Event<Int>>()
    val insertAllConversations = MutableLiveData<Unit>()
    val insertAllContacts = MutableLiveData<Unit>()
    val searchForContacts = MutableLiveData<List<Contact>>()
    val getContactByAddress = MutableLiveData<Contact?>()
    val getConversationByAddress = MutableLiveData<Conversation?>()
    val maxThreadIdInRom = MutableLiveData<Long>()

    fun updateContactChangeFromContentProviderToRoom(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.updateContactChangeFromContentProviderToRoom(context)
        }
    }

    fun getMessageCountFromRoom(){
        viewModelScope.launch(Dispatchers.IO){
            messageCountFromRoom.postValue(Event(smsRepository.getMessageCountFromRoom()))
        }
    }

    fun getMessageCountFromContentProvider(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            messageCountFromContentProvider.postValue(
                Event(
                    smsRepository.getMessageCountFromContentProvider(
                        context
                    )
                )
            )
        }
    }

    fun markConversationAsRead(threadId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationRepository.markConversationAsRead(threadId)
        }
    }

    fun getContactByAddress(address: String){
        viewModelScope.launch(Dispatchers.IO) {
            getContactByAddress.postValue(contactRepository.getContactByAddress(address))
        }
    }
    fun getConversationByAddress(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getConversationByAddress.postValue(conversationRepository.getConversationByAddress(address))
        }
    }

    fun markMessageAsRead(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            smsRepository.markMessageAsRead(id)
        }
    }

    fun insertMessage(message: Message) {
        viewModelScope.launch(Dispatchers.IO) {
            smsRepository.insertMessage(message)
        }
    }

    fun getMaxThreadIdInRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            maxThreadIdInRom.postValue(conversationRepository.getMaxThreadIdInRoom())
        }
    }

    fun upsertConversation(conversation: Conversation) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationRepository.upsertConversation(conversation)
        }
    }

    fun getContactLiveData(id: Int): LiveData<Contact> {
        return contactRepository.getContactLiveData(id)
    }

    fun insertAllConversationsToRoom(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            insertAllConversations.postValue(
                conversationRepository.insertAllConversationsToRoom(
                    context
                )
            )
        }
    }


    fun insertListMessage(context: Context, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            smsRepository.insertListMessage(context, position)
        }
    }

    fun updateErrorMessage(context: Context, id: Long){
        viewModelScope.launch(Dispatchers.IO){
            smsRepository.updateErrorMessage(context,id)
        }
    }

    fun insertAllContactsToRoom(context: Context) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.d(throwable)
            }
        }) {
            insertAllContacts.postValue(contactRepository.insertAllContactsToRoom(context))
        }
    }

    fun insertAllMessageToRoom(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            smsRepository.insertAllMessageToRoom(context)
        }
    }

    fun updateConversationAndMessage(
        contactId: Int,
        photoLink: String?,
        contactName: String,
        address: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationRepository.updateConversationAndMessage(
                contactId,
                photoLink,
                contactName,
                address
            )
        }
    }


    fun sendSMS(phoneNumber: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isSendMessageSuccess.postValue(Event(smsRepository.sendSMS(phoneNumber, message)))
        }
    }

    fun sendMessage(phoneNumber: String, messageContent: String){
        viewModelScope.launch(Dispatchers.IO){
            smsRepository.sendSMS(phoneNumber,messageContent)
        }
    }

    fun searchForContacts(queryString: String){
        viewModelScope.launch(Dispatchers.IO){
            searchForContacts.postValue(contactRepository.searchForContacts(queryString))
        }
    }


    fun getWallPaperLandscapedSelected(): String? {
        return sharedPreferenceRepository.getWallpaperLandscapedSelected()
    }

    fun getWallPaperColorSelected(): Int {
        return sharedPreferenceRepository.getWallpaperColorSelected()
    }

    fun getFontSelected(): String? {
        return sharedPreferenceRepository.getFontSelected()
    }
}