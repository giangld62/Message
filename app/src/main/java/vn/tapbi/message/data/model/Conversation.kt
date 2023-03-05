package vn.tapbi.message.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var threadId: Long = 0,
    var address: String = "",
    var recipientIds: Int = 0,
    var contactId: Int? = -1,
    var date: Long = 0,
    val read: Int = 1,
    var contactName: String? = "",
    var photoLink: String? = "",
    var snippet: String? = "",
    var isSentSuccessfully: Boolean = true
)