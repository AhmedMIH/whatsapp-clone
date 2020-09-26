package com.example.whatsappclone.data.firebase

import com.example.whatsappclone.data.model.ChatList
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.notifications.Token
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Observable

class FirebaseRealTimeDb() : FirebaseSource() {


    fun retrieveChatListChildren(userId: String): Observable<List<ChatList>> {
        return Observable.create { emitter ->
            val userChatArrayList = ArrayList<ChatList>()
            val ref = firebaseDatabase.reference.child("ChatList")
                .child(userId)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    userChatArrayList.clear()
                    for (snapshot in p0.children) {
                        val singleChatList = snapshot.getValue(ChatList::class.java)
                        userChatArrayList.add(singleChatList!!)
                    }
                    emitter.onNext(userChatArrayList)
                    emitter.onComplete()
                }
            })

        }
    }

    fun retrieveUserChildren(): Observable<List<Users>> {
        return Observable.create { emitter ->
            val users = ArrayList<Users>()
            val ref = FirebaseDatabase.getInstance().reference.child("Users")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    users.clear()
                    for (snapshot in p0.children) {
                        val user = snapshot.getValue(Users::class.java)
                        users.add(user!!)
                    }
                    emitter.onNext(users)
                    emitter.onComplete()
                }
            })
        }
    }

    fun searchForUser(string: String): Observable<List<Users>> {
        return Observable.create { emitter ->
            val users = ArrayList<Users>()
            val queryUser = FirebaseDatabase.getInstance().reference
                .child("Users")
                .orderByChild("search")
                .startAt(string)
                .endAt(string + "\uf8ff")
            queryUser.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }
                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot in p0.children) {
                        val user: Users? = snapshot.getValue(Users::class.java)
                        users.add(user!!)
                    }
                    emitter.onNext(users)
                    emitter.onComplete()
                }
            })
        }
    }

    fun updateToken(userId: String) {
        val token = FirebaseInstanceId.getInstance().token
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token)
        ref.child(userId).setValue(token1)
    }
}