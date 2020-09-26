package com.example.whatsappclone.data.repositories

import com.example.whatsappclone.data.firebase.FirebaseSourceAuth

class AuthRepository(private val firebaseAuth: FirebaseSourceAuth) :
    BaseRepository(firebaseAuth) {
    fun login(email: String, password: String) = firebaseAuth.login(email, password)

    fun register(email: String, password: String) = firebaseAuth.register(email, password)

    fun logout() = firebaseAuth.logout()

    fun createUserInDb(username: String) = firebaseAuth.createUserInDb(username)

    fun updateStatus(child: String, childrenName: String) =
        firebaseAuth.updateChildren(child, childrenName)


}