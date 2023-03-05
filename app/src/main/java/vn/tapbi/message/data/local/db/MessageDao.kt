package vn.tapbi.message.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import vn.tapbi.message.data.model.Conversation
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.utils.Event

@Dao
interface MessageDao {
    @Query("select * from Message")
    fun getAllMessage(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListMessage(messages: List<Message>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)

    @Query("update Message set date =:date, threadId =:threadId, type = 2 where id =:id")
    fun updateErrorMessage(id: Long, date: Long, threadId: Long)

    @Query("select * from Message where threadId = :threadId or address =:address order by date")
    fun getAllMessageOfTheConversation(threadId: Long?, address: String?): LiveData<List<Message>>

    @Query("select * from Message where body like :queryString order by threadId")
    fun searchForMessageBody(queryString: String): List<Message>

    @Query("update Message set read = 1 where id = :id")
    fun markMessageAsRead(id: Long)

    @Query("update Message set photoLink = :photoLink, contactName =:contactName, contactId =:contactId where address = :address")
    fun updateMessage(
        contactId: Int,
        photoLink: String?,
        contactName: String,
        address: String
    )

    @Query("update Message set photoLink = null, contactName = address, contactId =-1 where address !=:address and contactId =:contactId")
    fun updateMessage2(
        contactId: Int,
        address: String
    )

    @Query("select count(id) from Message where type != 5")
    fun getMessageCountFromRoomLiveData(): Int

    @Query("select count(id) from Message where threadId =:threadId")
    fun getMessageCountOfConversation(threadId: Long): Int

    @Query("delete from Message where address =:address")
    fun deleteAllMessageWithSameThreadId(address: String)

    @Query("select * from Message where address =:address")
    fun getMessageByAddress(address: String): Message?

}