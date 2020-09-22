package com.example.whatsappclone.data.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Completable

class FirebaseSource {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val userInfo = MutableLiveData<Users>()

    fun login(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun register(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

    fun createUserInDb(userName: String) = Completable.create { emitter ->
        val ref: DatabaseReference = firebaseDatabase.reference
            .child("Users")
            .child(currentUser()!!.uid)
        val userHashMap = HashMap<String, Any>()
        userHashMap["uid"] = currentUser()!!.uid
        userHashMap["username"] = userName
        userHashMap["profile"] = ""
        userHashMap["cover"] = ""
        userHashMap["status"] = "offline"
        userHashMap["search"] = userName.toLowerCase()
        userHashMap["facebook"] = ""
        userHashMap["instagram"] = ""
        userHashMap["website"] = ""

        ref.updateChildren(userHashMap)
            .addOnCompleteListener {
                if (!emitter.isDisposed) {
                    if (it.isSuccessful)
                        emitter.onComplete()
                    else
                        emitter.onError(it.exception!!)
                }
            }


    }

    fun retrieveUserInformation(): MutableLiveData<Users> {
        val ref = firebaseDatabase.reference
            .child("Users")
            .child(currentUser()!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                userInfo.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return  userInfo
    }

    fun updateStatus(status: String) = Completable.create {emitter ->
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentUser()!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap).addOnCompleteListener {
            if (!emitter.isDisposed){
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun deleteMassage(massage:Chat) = Completable.create {emitter ->
        val ref = firebaseDatabase.reference.child("Chats")
            .child(massage.massageID!!)
            .removeValue()
            .addOnCompleteListener {
                if (!emitter.isDisposed){
                    if (it.isSuccessful)
                        emitter.onComplete()
                    else
                        emitter.onError(it.exception!!)
                }
            }
    }
}