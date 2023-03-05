package vn.tapbi.message.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.tapbi.message.data.model.Conversation
import vn.tapbi.message.data.model.SearchResult
import vn.tapbi.message.data.repository.ConversationRepository
import vn.tapbi.message.data.repository.SmsRepository
import vn.tapbi.message.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val smsRepository: SmsRepository
) : BaseViewModel() {
    val searchMessages = MutableLiveData<List<SearchResult>>()
    val searchForConversation = MutableLiveData<List<Conversation>>()
    val conversationsLiveData = conversationRepository.getAllConversationsLiveData()

    fun searchForConversations(queryString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchForConversation.postValue(
                conversationRepository.searchForConversations(
                    queryString
                )
            )
        }
    }


    fun searchForMessages(queryString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchMessages.postValue(smsRepository.searchForMessages(queryString))
        }
    }
}