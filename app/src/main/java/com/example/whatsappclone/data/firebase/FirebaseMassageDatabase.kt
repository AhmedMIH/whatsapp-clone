package com.example.whatsappclone.data.firebase

import androidx.lifecycle.MutableLiveData
import com.example.whatsappclone.R
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.notifications.Data
import com.example.whatsappclone.notifications.MyResponse
import com.example.whatsappclone.notifications.Sender
import com.example.whatsappclone.notifications.Token
import com.example.whatsappclone.notifications.network.ApiService
import com.example.whatsappclone.notifications.network.Client
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Completable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirebaseMassageDatabase(private val client: Client) : FirebaseSource() {

    val chatList = MutableLiveData<List<Chat>>()
    fun deleteMassage(massage: Chat) = Completable.create { emitter ->
        val ref = firebaseDatabase.reference.child("Chats")
            .child(massage.massageID!!)
            .removeValue()
            .addOnCompleteListener {
                if (!emitter.isDisposed) {
                    if (it.isSuccessful)
                        emitter.onComplete()
                    else
                        emitter.onError(it.exception!!)
                }
            }
    }
    fun sendMassage(senderId: String, receiverId: String, massage: String, url: String) =
        Completable.create { emitter ->
            val reference = firebaseDatabase.reference
            val massageKey = reference.push().key
            val massageHashMap = HashMap<String, Any?>()
            massageHashMap["sender"] = senderId
            massageHashMap["receiver"] = receiverId
            massageHashMap["massage"] = massage
            massageHashMap["isSeen"] = false
            massageHashMap["url"] = url
            massageHashMap["massageID"] = massageKey
            reference.child("Chats").child(massageKey!!).setValue(massageHashMap)
                .addOnCompleteListener { task ->
                    if (!emitter.isDisposed) {
                        if (task.isSuccessful) {
                            emitter.onComplete()
                            val chatListReference =
                                FirebaseDatabase.getInstance().reference.child("ChatList")
                                    .child(senderId)
                                    .child(receiverId)
                            chatListReference.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {}

                                override fun onDataChange(p0: DataSnapshot) {
                                    if (p0.exists()) {
                                        chatListReference.child("id").setValue(receiverId)
                                    }
                                    val chatListReceiverRef =
                                        FirebaseDatabase.getInstance().reference.child("ChatList")
                                            .child(receiverId)
                                            .child(senderId)
                                    chatListReceiverRef.child("id").setValue(senderId)
                                }
                            })
                        } else
                            emitter.onError(task.exception!!)
                    }
                }
        }

    fun retrieveMassage(senderId: String, receiverId: String): MutableLiveData<List<Chat>> {
        val chatArrayList = ArrayList<Chat>()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatArrayList.clear()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if ((chat!!.receiver == senderId && chat.sender == receiverId)
                        || (chat.receiver == receiverId && chat.sender == senderId)
                    ) {
                        chatArrayList.add(chat)
                    }
                }
                chatList.value = chatArrayList
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return chatList
    }

    fun seenMassage(receiverId: String, SenderId: String) = Completable.create { emitter ->
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver == SenderId && chat.sender.equals(receiverId)) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isSeen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

        })
    }

    fun sendNotification(
        receiverId: String,
        senderId: String,
        username: String?,
        massage: String
    ) = Completable.create { emitter ->
        val apiService = client.getClient("https://fcm.googleapis.com/")?.create(ApiService::class.java)
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val token = snapshot.getValue(Token::class.java)
                    val data =
                        Data(
                            senderId,
                            R.mipmap.ic_launcher,
                            "$username : $massage",
                            "new massage",
                            receiverId
                        )
                    val sender = Sender(data, token!!.token.toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                emitter.onError(t)
                            }

                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {

                                if (!emitter.isDisposed) {
                                    if (response.code() == 200)
                                        emitter.onComplete()
                                }
                            }

                        })
                }
            }
        })
    }

}