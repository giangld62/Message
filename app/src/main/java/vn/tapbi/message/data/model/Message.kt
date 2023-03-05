package vn.tapbi.message.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var threadId: Long = 0,
    var date: Long = 0,
    var body: String = "",
    var read: Int = 0,
    var photoLink: String? = "",
    var contactName: String? = "",
    var contactId: Int? = -1,
    var type: Int? = 0,
    var address: String? = ""
)
