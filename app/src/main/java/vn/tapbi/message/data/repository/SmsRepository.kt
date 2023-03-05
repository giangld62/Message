package vn.tapbi.message.data.repository

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.SmsManager
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import timber.log.Timber
import vn.tapbi.message.common.Constant
import vn.tapbi.message.common.Constant.GET_CONTACT_ID
import vn.tapbi.message.common.Constant.GET_CONTACT_NAME_OR_ADDRESS
import vn.tapbi.message.common.Constant.GET_PHOTO_URI
import vn.tapbi.message.data.local.SharedPreferenceHelper
import vn.tapbi.message.data.local.db.ConversationDao
import vn.tapbi.message.data.local.db.MessageDao
import vn.tapbi.message.data.model.Conversation
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.data.model.SearchResult
import javax.inject.Inject


class SmsRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) {

    fun insertAllMessageToRoom(context: Context) {
        if (sharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_SMS_FIRST_TIME, true)) {
            messageDao.insertListMessage(
                getAllMessagesFromContentProvider(
                    context,
                    null,
                    null,
                    null,
                    0
                )
            )
            sharedPreferenceHelper.storeBoolean(Constant.LOAD_ALL_SMS_FIRST_TIME, false)
        }
    }

    fun updateErrorMessage(context: Context, id: Long){
        val message = getAllMessagesFromContentProvider(context,null,null,"_id desc limit 1",0)[0]
        messageDao.updateErrorMessage(
            id = id,
            date = message.date,
            threadId = message.threadId
        )
    }

    fun insertListMessage(context: Context, position: Int) {
        Timber.d("giangledinh insertListMessage")
        val messages = getAllMessagesFromContentProvider(context, null, null, "date asc", position)
        messageDao.insertListMessage(messages)
        for (item in messages) {
            val conversation = conversationDao.getConversationByAddress(item.address!!)
            if (conversation == null) {
                conversationDao.upsertConversation(
                    Conversation(
                        address = item.address!!,
                        threadId = item.threadId,
                        contactId = item.contactId,
                        date = item.date,
                        read = item.read,
                        contactName = item.contactName,
                        photoLink = item.photoLink,
                        snippet = if (item.body.length > 59) item.body.substring(0, 60) else item.body
                    )
                )
            }
            else{
                conversationDao.upsertConversation(
                    Conversation(
                        id = conversation.id,
                        address = item.address!!,
                        threadId = item.threadId,
                        contactId = item.contactId,
                        date = item.date,
                        read = item.read,
                        contactName = item.contactName,
                        photoLink = item.photoLink,
                        snippet = if (item.body.length > 59) item.body.substring(0, 60) else item.body
                    )
                )
            }
        }
    }

    fun insertMessage(message: Message) {
        messageDao.insertMessage(message)
    }

    fun getAllMessagesOfTheConversationFromContentProvider(
        context: Context,
        threadId: Long
    ): List<Message> {
        return getAllMessagesFromContentProvider(
            context,
            "thread_id = ?",
            arrayOf(threadId.toString()),
            "date asc",
            0
        )
    }


    fun searchForMessages(queryString: String): List<SearchResult>{
        val searchResults = arrayListOf<SearchResult>()
        val contactNameOrAddressResults = conversationDao.searchForContactName(queryString)
        for(item in contactNameOrAddressResults){
            val messageCount = messageDao.getMessageCountOfConversation(item.threadId)
            searchResults.add(
                SearchResult(
                    contactName = item.contactName,
                    address = item.address,
                    messageCount = messageCount,
                    contactId = item.contactId,
                    threadId = item.threadId
                )
            )
        }
        val messageBodyResults = messageDao.searchForMessageBody(queryString)
        if (messageBodyResults.isNotEmpty()) {
            var startPosition = 0
            var currentThreadId = messageBodyResults[startPosition].threadId
            for( i in messageBodyResults.indices){
                if( messageBodyResults[i].threadId != currentThreadId){
                    val messages = arrayListOf<Message>()
                    messages.addAll(messageBodyResults.subList(startPosition,i))
                    startPosition = i
                    searchResults.add(
                        SearchResult(
                            contactName = messageBodyResults[i-1].contactName,
                            address = messageBodyResults[i-1].address,
                            contactId = messageBodyResults[i-1].contactId,
                            threadId = messageBodyResults[i-1].threadId,
                            messages = messages
                        )
                    )
                    currentThreadId = messageBodyResults[i].threadId
                }
                else if(messageBodyResults[i].threadId == messageBodyResults[messageBodyResults.size-1].threadId){
                    searchResults.add(
                        SearchResult(
                            contactName = messageBodyResults[i].contactName,
                            address = messageBodyResults[i].address,
                            contactId = messageBodyResults[i].contactId,
                            threadId = messageBodyResults[i].threadId,
                            messages = messageBodyResults.subList(i,messageBodyResults.size)
                        )
                    )
                    break
                }
            }
        }
        return searchResults
    }

    fun markMessageAsRead(id: Long) {
        messageDao.markMessageAsRead(id)
    }


    fun getAllMessagesOfTheConversationLiveData(threadId: Long?, address: String?): LiveData<List<Message>> {
        return messageDao.getAllMessageOfTheConversation(threadId, address)
    }

    fun getMessageCountFromRoom(): Int {
        return messageDao.getMessageCountFromRoomLiveData()
    }


    fun getMessageCountFromContentProvider(context: Context): Int {
        var messageCount = 0
        val cursor =
            context.contentResolver.query(
                Uri.parse("content://sms"),
                null,
                null,
                null,
                null
            )
        if (cursor != null) {
            messageCount = cursor.count
        }
        cursor?.close()
        return messageCount
    }

    private fun getAllMessagesFromContentProvider(
        context: Context,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
        position: Int
    ): List<Message> {
        val messageList = arrayListOf<Message>()
        val projection = arrayOf("date", "body", "read", "thread_id", "type", "address")
        val cursor =
            context.contentResolver.query(
                Uri.parse("content://sms"),
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                do {
                    val date = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
                    val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                    val read = cursor.getInt(cursor.getColumnIndexOrThrow("read"))
                    val threadId = cursor.getLong(cursor.getColumnIndexOrThrow("thread_id"))
                    val type = cursor.getInt(cursor.getColumnIndexOrThrow("type"))
                    val address = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("address"))
                        ?.replace("+84", "0")?.replace("-", "")?.replace("\\s+".toRegex(), "")
                        ?.replace("(", "")?.replace(")", "")
                    val numberPhone = getContactInfoByPhoneNumber(
                        context,
                        address,
                        GET_CONTACT_NAME_OR_ADDRESS
                    )
                    val thumbnailUri = getContactInfoByPhoneNumber(
                        context,
                        address,
                        GET_PHOTO_URI
                    )
                    val contactId = getContactInfoByPhoneNumber(context, address, GET_CONTACT_ID)

                    messageList.add(
                        Message(
                            date = date,
                            body = body,
                            read = read,
                            contactName = numberPhone,
                            photoLink = thumbnailUri,
                            type = type,
                            address = address,
                            threadId = threadId,
                            contactId = contactId?.toInt()
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return messageList
    }

    fun getAddressById(context: Context, threadId: Long): String {
        var phoneNumber = ""
        val uri = ContentUris.withAppendedId(
            Uri.parse("content://mms-sms/canonical-address"),
            threadId
        )
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor!!.moveToFirst()) {
                phoneNumber = cursor.getString(0).replace("+84", "0").replace("\\s+".toRegex(), "")
            }
        } finally {
            cursor!!.close()
        }
        return phoneNumber
    }

    fun getContactById(
        context: Context,
        threadId: Long,
        getMode: Int
    ): String? {
        var phoneNumber: String? = ""
        val uri = ContentUris.withAppendedId(
            Uri.parse("content://mms-sms/canonical-address"),
            threadId
        )
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor!!.moveToFirst()) {
                phoneNumber = getContactInfoByPhoneNumber(context, cursor.getString(0), getMode)
            }
        } finally {
            cursor!!.close()
        }
        return phoneNumber
    }

    @SuppressLint("Range")
    fun getContactInfoByPhoneNumber(context: Context, phoneNumber: String?, getMode: Int): String? {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup.NORMALIZED_NUMBER,
            ContactsContract.PhoneLookup.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        )
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        var name: String? = null
        var mPhoneNumber = phoneNumber
        var photoLink: String? = null
        var contactId: String? = "-1"
        try {
            if (cursor!!.moveToFirst()) {
                cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NORMALIZED_NUMBER))
                    .also { mPhoneNumber = it }
                cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                    .also { name = it }
                cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI))
                    .also { photoLink = it }

                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    .also { contactId = it }
            }
        } finally {
            cursor!!.close()
        }


        return when (getMode) {
            GET_CONTACT_NAME_OR_ADDRESS -> name ?: mPhoneNumber
            GET_PHOTO_URI -> photoLink
            GET_CONTACT_ID -> contactId
            else -> throw IllegalArgumentException("Unknown mode")
        }

    }

    fun sendSMS(phoneNumber: String, message: String): Boolean {
        var isSuccess = false
        try {

            // Get the default instance of the SmsManager
//            val smsManager: SmsManager = requireContext().getSystemService(SmsManager::class.java)
            val smsManager = SmsManager.getDefault()
            // Send Message
            smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )
            isSuccess = true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return isSuccess
    }


}