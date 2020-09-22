package com.example.whatsappclone.modelClass

data class Chat(
    var sender: String? = "",
    var massage: String? = "",
    var receiver: String? = "",
    var isSeen: Boolean? = false,
    var url: String? = "",
    var massageID: String? = ""
)