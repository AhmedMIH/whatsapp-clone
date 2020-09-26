package com.example.whatsappclone.data.repositories

import com.example.whatsappclone.data.firebase.FirebaseMassageDatabase
import com.example.whatsappclone.data.model.Chat

class MassageRepository (private val firebaseMassageDatabase: FirebaseMassageDatabase) :
    BaseRepository(firebaseMassageDatabase) {

    fun deleteMassage(massage: Chat) = firebaseMassageDatabase.deleteMassage(massage)

    fun sendMassage(senderId: String, receiverId: String, massage: String, url: String) =
        firebaseMassageDatabase.sendMassage(senderId, receiverId, massage, url)

    fun retrieveMassage(senderId: String, receiverId: String) =
        firebaseMassageDatabase.retrieveMassage(senderId, receiverId)

    fun seenMassage(receiverId: String, SenderId: String) =
        firebaseMassageDatabase.seenMassage(receiverId, SenderId)

    fun sendNotification(
        receiverId: String,
        senderId: String,
        username: String?,
        massage: String
    ) = firebaseMassageDatabase.sendNotification(receiverId, senderId, username, massage)

}