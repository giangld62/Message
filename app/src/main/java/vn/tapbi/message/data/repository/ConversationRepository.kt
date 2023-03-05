package vn.tapbi.message.data.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import timber.log.Timber
import vn.tapbi.message.common.Constant
import vn.tapbi.message.data.local.SharedPreferenceHelper
import vn.tapbi.message.data.local.db.ConversationDao
import vn.tapbi.message.data.local.db.MessageDao
import vn.tapbi.message.data.model.Conversation
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val conversationDao: ConversationDao,
    private val smsRepository: SmsRepository,
    private val messageDao: MessageDao
) {

    fun insertAllConversationsToRoom(context: Context) {
        if (sharedPreferenceHelper.getBoolean(
                Constant.LOAD_ALL_CONVERSATION_FIRST_TIME,
                true
            )
        ) {
            conversationDao.insertAllConversation(
                getAllConversationsFromContentProvider(context)
            )
            sharedPreferenceHelper.storeBoolean(Constant.LOAD_ALL_CONVERSATION_FIRST_TIME, false)
        }
    }

    fun getAllConversationsLiveData(): LiveData<List<Conversation>> {
        return conversationDao.getAllConversationsLiveData()
    }

    fun markConversationAsRead(threadId: Long) {
        conversationDao.markConversationAsRead(threadId)
    }

    fun updateConversationAndMessage(
        contactId: Int,
        photoLink: String?,
        contactName: String,
        address: String
    ) {
        conversationDao.updateConversation(contactId, photoLink, contactName, address)
        messageDao.updateMessage(contactId, photoLink, contactName, address)
    }

    fun upsertConversation(conversation: Conversation) {
        conversationDao.upsertConversation(conversation)
    }

    fun searchForConversations(queryString: String): List<Conversation> {
        return if (queryString != "") conversationDao.searchForConversations(queryString)
        else conversationDao.getAllConversations()
    }

    fun getConversationByAddress(address: String): Conversation?{
        return conversationDao.getConversationByAddress(address)
    }

    fun getMaxThreadIdInRoom(): Long{
        return conversationDao.getConversationWithMaxThreadId().threadId
    }

    private fun getAllConversationsFromContentProvider(context: Context): List<Conversation> {
        val conversations = arrayListOf<Conversation>()
        val projection = arrayOf("_id", "date", "snippet", "read", "recipient_ids")
        val cursor =
            context.contentResolver.query(
                Uri.parse("content://mms-sms/conversations?simple=true"),
                projection,
                null,
                null,
                null
            )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val threadId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
                    val date = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
                    val snippet = cursor.getString(cursor.getColumnIndexOrThrow("snippet"))
                    val read = cursor.getInt(cursor.getColumnIndexOrThrow("read"))
                    val recipientIds = cursor.getInt(cursor.getColumnIndexOrThrow("recipient_ids"))
                    val phoneNumber = smsRepository.getContactById(
                        context,
                        recipientIds.toLong(),
                        Constant.GET_CONTACT_NAME_OR_ADDRESS
                    )?.replace("+84", "0")?.replace("-", "")?.replace("\\s+".toRegex(), "")
                        ?.replace("(", "")?.replace(")", "")
                    val thumbnailUri = smsRepository.getContactById(
                        context,
                        recipientIds.toLong(),
                        Constant.GET_PHOTO_URI
                    )
                    val contactId =
                        smsRepository.getContactById(
                            context,
                            recipientIds.toLong(),
                            Constant.GET_CONTACT_ID
                        )?.toInt()
                    val address = smsRepository.getAddressById(context, recipientIds.toLong()).replace("+84", "0").replace("-", "").replace("\\s+".toRegex(), "")
                        .replace("(", "").replace(")", "")
                    conversations.add(
                        Conversation(
                            threadId = threadId,
                            recipientIds = recipientIds,
                            contactId = contactId,
                            date = date,
                            snippet = snippet,
                            read = read,
                            contactName = phoneNumber,
                            photoLink = thumbnailUri,
                            address = address
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return conversations
    }
}