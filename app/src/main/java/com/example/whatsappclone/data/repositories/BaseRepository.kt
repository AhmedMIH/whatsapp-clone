package com.example.whatsappclone.data.repositories

import com.example.whatsappclone.data.firebase.FirebaseSource
import javax.inject.Inject

open class BaseRepository @Inject constructor(val firebase: FirebaseSource) {
    fun currentUser() = firebase.currentUser()

    fun getCurrentUserInfo(userId: String) = firebase.getCurrentUserInfo(userId)
    fun getReceiverUserInfo(userId: String) = firebase.getReceiverUserInfo(userId)

}