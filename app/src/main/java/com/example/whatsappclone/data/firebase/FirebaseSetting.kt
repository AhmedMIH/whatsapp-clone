package com.example.whatsappclone.data.firebase

import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable

class FirebaseSetting:FirebaseSource() {
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