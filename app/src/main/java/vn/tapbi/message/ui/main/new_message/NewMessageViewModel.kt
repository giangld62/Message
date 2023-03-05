package vn.tapbi.message.ui.main.new_message

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.data.model.Conversation
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.data.repository.ContactRepository
import vn.tapbi.message.data.repository.ConversationRepository
import vn.tapbi.message.data.repository.SmsRepository
import vn.tapbi.message.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class NewMessageViewModel @Inject constructor() : BaseViewModel() {
}