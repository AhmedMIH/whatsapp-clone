package com.example.whatsappclone.data.firebase

import androidx.lifecycle.MutableLiveData
import com.example.whatsappclone.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable

open class FirebaseSource {
    val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    fun currentUser() = firebaseAuth.currentUser
    val userInfo = MutableLiveData<Users>()
    val reveiverUserInfo = MutableLiveData<Users>()

    fun getCurrentUserInfo(userId: String): Observable<Users> {
        return Observable.create { emitter ->
            val ref = firebaseDatabase.reference
                .child("Users")
                .child(userId)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                 //   userInfo.postValue(user)
                    emitter.onNext(user!!)
                    emitter.onComplete()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        }
    }
//    fun getCurrentUserInfo(userId: String): MutableLiveData<Users> {
//        val ref = firebaseDatabase.reference
//            .child("Users")
//            .child(userId)
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val user = snapshot.getValue(Users::class.java)
//                userInfo.postValue(user)
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
//        return userInfo
//    }

    fun getReceiverUserInfo(userId: String): MutableLiveData<Users> {

        val ref = firebaseDatabase.reference
            .child("Users")
            .child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                reveiverUserInfo.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return reveiverUserInfo
    }

}