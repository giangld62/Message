package vn.tapbi.message.data.model

data class SearchResult(
    var contactName: String? = "",
    var address: String? = "",
    var contactId: Int? = 0,
    var threadId: Long = 0,
    var messageCount: Int = 0,
    var messages: List<Message> = arrayListOf()
)