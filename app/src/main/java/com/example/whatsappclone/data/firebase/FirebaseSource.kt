package com.example.whatsappclone.data.firebase

import androidx.lifecycle.MutableLiveData
import com.example.whatsappclone.R
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.data.model.ChatList
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.notifications.Data
import com.example.whatsappclone.notifications.MyResponse
import com.example.whatsappclone.notifications.Sender
import com.example.whatsappclone.notifications.Token
import com.example.whatsappclone.notifications.network.ApiService
import com.example.whatsappclone.notifications.network.ApiServiceImpl
import com.example.whatsappclone.notifications.network.Client
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Completable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.collections.set

class FirebaseSource @Inject constructor() {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val userInfo = MutableLiveData<Users>()
    val chatList = MutableLiveData<List<Chat>>()
    val userChatList = MutableLiveData<List<ChatList>>()
    val usersList = MutableLiveData<List<Users>>()

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

    fun getCurrentUserInfo(userId: String): MutableLiveData<Users> {
        val ref = firebaseDatabase.reference
            .child("Users")
            .child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                userInfo.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return userInfo
    }

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

    fun retrieveUserInformation(userId: String): MutableLiveData<Users> {
        val ref = firebaseDatabase.reference
            .child("Users")
            .child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                userInfo.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return userInfo
    }

    fun updateStatus(status: String) = Completable.create { emitter ->
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentUser()!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

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

//    fun sendNotification(
//        receiverId: String,
//        senderId: String,
//        username: String?,
//        massage: String
//    ) = Completable.create { emitter ->
//        val apiService = apiService.sendNotification()
//        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
//        val query = ref.orderByKey().equalTo(receiverId)
//        query.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {}
//            override fun onDataChange(p0: DataSnapshot) {
//                for (snapshot in p0.children) {
//                    val token = snapshot.getValue(Token::class.java)
//                    val data =
//                        Data(
//                            senderId,
//                            R.mipmap.ic_launcher,
//                            "$username : $massage",
//                            "new massage",
//                            receiverId
//                        )
//                    val sender = Sender(data, token!!.token.toString())
//                    apiService!!.sendNotification(sender)
//                        .enqueue(object : Callback<MyResponse> {
//                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
//                                emitter.onError(t)
//                            }
//
//                            override fun onResponse(
//                                call: Call<MyResponse>,
//                                response: Response<MyResponse>
//                            ) {
//
//                                if (!emitter.isDisposed) {
//                                    if (response.code() == 200)
//                                        emitter.onComplete()
//                                }
//                            }
//
//                        })
//                }
//            }
//        })
//    }

    fun retrieveChatListChildren(userId: String): MutableLiveData<List<ChatList>> {
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
                userChatList.postValue(userChatArrayList)
            }
        })
        return userChatList
    }

    fun retrieveUserChildren(chatList: List<ChatList>): MutableLiveData<List<Users>> {
        val users = ArrayList<Users>()
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                users.clear()
                for (snapshot in p0.children) {
                    val user = snapshot.getValue(Users::class.java)
                    for (eachChatList in chatList) {
                        if (user!!.uid == eachChatList.id) {
                            users.add(user)
                        }
                    }
                }
                usersList.postValue(users)
            }
        })
        return usersList
    }
}