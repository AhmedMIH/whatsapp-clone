package com.example.whatsappclone.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.whatsappclone.data.firebase.FirebaseSource
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.data.model.Users

class UserRepository(private val firebase: FirebaseSource) {
    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String) = firebase.register(email, password)

    fun currentUser() = firebase.currentUser()

    fun logout() = firebase.logout()

    fun createUserInDb(username:String) = firebase.createUserInDb(username)

    fun retrieveUserInformation() = firebase.retrieveUserInformation()

    fun updateStatus(state:String) = firebase.updateStatus(state)

    fun deleteMassage(massage:Chat) = firebase.deleteMassage(massage)
}