package com.example.whatsappclone.data.repositories

import com.example.whatsappclone.data.firebase.FirebaseRealTimeDb
import com.example.whatsappclone.data.model.Users
import io.reactivex.Observable


class ChatsListRepository(private val firebaseRealTimeDb: FirebaseRealTimeDb) :
    BaseRepository(firebaseRealTimeDb) {

    fun retrieveChatListChildren(userId: String) = firebaseRealTimeDb.retrieveChatListChildren(userId)

    fun retrieveUserChildren() = firebaseRealTimeDb.retrieveUserChildren()

    fun searchForUser(string: String) =  firebaseRealTimeDb.searchForUser(string)

    fun updateToken(userId: String) = firebaseRealTimeDb.updateToken(userId)
}