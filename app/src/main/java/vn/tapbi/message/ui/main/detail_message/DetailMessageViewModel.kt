package vn.tapbi.message.ui.main.detail_message

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.data.repository.ConversationRepository
import vn.tapbi.message.data.repository.SmsRepository
import vn.tapbi.message.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DetailMessageViewModel @Inject constructor(
    private val smsRepository: SmsRepository,
) : BaseViewModel() {
    val messages = MutableLiveData<List<Message>>()

    fun getMessagesOfTheConversationLiveData(
        threadId: Long?,
        address: String?
    ): LiveData<List<Message>> {
        return smsRepository.getAllMessagesOfTheConversationLiveData(threadId, address)
    }

    fun getMessagesOfTheConversationFromContentProvider(context: Context, threadId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            messages.postValue(
                smsRepository.getAllMessagesOfTheConversationFromContentProvider(
                    context,
                    threadId
                )
            )
        }
    }
}