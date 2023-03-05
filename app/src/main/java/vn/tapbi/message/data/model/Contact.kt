package vn.tapbi.message.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Contact(
    @PrimaryKey
    var contactId: Int = 0,
    var rawContactId: Int = 0,
    var name: String = "",
    var phoneNumber: String = "",
    var photoUri: String? = null,
    var contactLastUpdateTime: Long = 0
)
