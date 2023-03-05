package vn.tapbi.message.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import vn.tapbi.message.data.model.Conversation

@Dao
interface ConversationDao {
    @Query("select * from Conversation order by date desc")
    fun getAllConversationsLiveData(): LiveData<List<Conversation>>

    @Query("select * from Conversation order by date desc")
    fun getAllConversations(): List<Conversation>

    @Query("select * from Conversation where address =:address")
    fun getConversationByAddress(address: String): Conversation?

    @Query("select * from Conversation order by threadId desc limit 1")
    fun getConversationWithMaxThreadId(): Conversation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllConversation(conversations: List<Conversation>)

    @Query("update Conversation set read = 1 where threadId = :threadId")
    fun markConversationAsRead(threadId: Long)

    @Query("update Conversation set photoLink = :photoLink, contactName =:contactName, contactId =:contactId where address =:address")
    fun updateConversation(
        contactId: Int,
        photoLink: String?,
        contactName: String,
        address: String
    )

    @Query("update Conversation set photoLink = null, contactName = address, contactId =-1 where address !=:address and contactId =:contactId")
    fun updateConversation2(
        contactId: Int,
        address: String
    )

    @Query("select * from Conversation where contactName like :queryString or snippet like :queryString")
    fun searchForConversations(queryString: String): List<Conversation>

    @Query("select * from Conversation where contactName like :queryString")
    fun searchForContactName(queryString: String): List<Conversation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertConversation(conversation: Conversation)

    @Delete
    fun deleteConversation(conversation: Conversation)
}