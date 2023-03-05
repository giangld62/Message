package vn.tapbi.message.common.models

import vn.tapbi.message.data.model.Message


data class MessageEvent (
    var typeEvent: Int = 0,
    var stringValue: String = "",
    var message: Message? =null
)

