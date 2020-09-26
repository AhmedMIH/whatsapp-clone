package com.example.whatsappclone.data.firebase

import com.google.firebase.database.*
import io.reactivex.Completable
import javax.inject.Inject
import kotlin.collections.set

class FirebaseSourceAuth  : FirebaseSource(){

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

    fun updateChildren(child: String,childrenName:String) = Completable.create { emitter ->
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentUser()!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap[childrenName] = child
        ref.updateChildren(hashMap).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

}