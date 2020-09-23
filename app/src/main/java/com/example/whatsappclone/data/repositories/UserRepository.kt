package com.example.whatsappclone.data.repositories

import com.example.whatsappclone.data.firebase.FirebaseSource
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.data.model.ChatList
import javax.inject.Inject

class UserRepository @Inject constructor(private val firebase: FirebaseSource) {
    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String) = firebase.register(email, password)

    fun currentUser() = firebase.currentUser()

    fun getCurrentUserInfo(userId: String) = firebase.getCurrentUserInfo(userId)

    fun logout() = firebase.logout()

    fun createUserInDb(username: String) = firebase.createUserInDb(username)

    fun retrieveUserInformation(userId: String) = firebase.retrieveUserInformation(userId)

    fun updateStatus(state: String) = firebase.updateStatus(state)

    fun deleteMassage(massage: Chat) = firebase.deleteMassage(massage)

    fun sendMassage(senderId: String, receiverId: String, massage: String, url: String) =
        firebase.sendMassage(senderId, receiverId, massage, url)

    fun retrieveMassage(senderId: String, receiverId: String) =
        firebase.retrieveMassage(senderId, receiverId)

    fun seenMassage(receiverId: String, SenderId: String) =
        firebase.seenMassage(receiverId, SenderId)

//    fun sendNotification(
//        receiverId: String,
//        senderId: String,
//        username: String?,
//        massage: String
//    ) = firebase.sendNotification(receiverId, senderId, username, massage)

    fun retrieveChatListChildren(userId: String) = firebase.retrieveChatListChildren(userId)

    fun retrieveUserChildren(chatList: List<ChatList>) = firebase.retrieveUserChildren(chatList)
}