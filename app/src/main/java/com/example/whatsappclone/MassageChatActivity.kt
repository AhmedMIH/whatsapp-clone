package com.example.whatsappclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.adapter.ChatsAdapter
import com.example.whatsappclone.fragments.ApiService
import com.example.whatsappclone.modelClass.Chat
import com.example.whatsappclone.modelClass.Users
import com.example.whatsappclone.notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_massage_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MassageChatActivity : AppCompatActivity() {
    var userIdVisit: String? = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter: ChatsAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recyclerView: RecyclerView
    var reference: DatabaseReference? = null
    var notify = false
    var apiService: ApiService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_massage_chat)
        apiService = Client.getClient("https://fcm.googleapis.com/")!!
            .create(ApiService::class.java)


        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")

        val toolbar = findViewById<Toolbar>(R.id.toolbar_massage_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        profile_image_massage_chat.setOnClickListener {
            val intent = Intent(this, VisitUserProfileActivity::class.java)
            intent.putExtra("visit_id", userIdVisit)
            startActivity(intent)
        }
        username_massage_chat.setOnClickListener {
            val intent = Intent(this, VisitUserProfileActivity::class.java)
            intent.putExtra("visit_id", userIdVisit)
            startActivity(intent)
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerView = findViewById(R.id.recycler_view_massage_chat)
        recyclerView.setHasFixedSize(true)
        val linerLayoutManager = LinearLayoutManager(this)
        linerLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linerLayoutManager

        reference =
            FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit!!)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)
                username_massage_chat.text = user!!.username
                Picasso.get().load(user.profile).into(profile_image_massage_chat)
                retrieveMassages(firebaseUser!!.uid, userIdVisit, user.profile)
            }

        })

        send_massage_btn.setOnClickListener {
            notify = true
            val massage = text_massage.text.toString()
            if (massage == "") {
                Toast.makeText(this, "please write a massage first  ", Toast.LENGTH_LONG).show()
            } else {
                sendMassageToUser(firebaseUser!!.uid, userIdVisit, massage)
            }
            text_massage.setText("")
        }

        attach_image_file_btn.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 483)

        }

        seenMassage(userIdVisit!!)
    }


    private fun sendMassageToUser(senderId: String, receiverId: String?, massage: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val massageKey = reference.push().key
        val massageHashMap = HashMap<String, Any?>()
        massageHashMap["sender"] = senderId
        massageHashMap["receiver"] = receiverId
        massageHashMap["massage"] = massage
        massageHashMap["isSeen"] = false
        massageHashMap["url"] = ""
        massageHashMap["massageID"] = massageKey
        reference.child("Chats").child(massageKey!!).setValue(massageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatListReference =
                        FirebaseDatabase.getInstance().reference.child("ChatList")
                            .child(firebaseUser!!.uid)
                            .child(receiverId!!)
                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                chatListReference.child("id").setValue(userIdVisit)
                            }
                            val chatListReceiverRef =
                                FirebaseDatabase.getInstance().reference.child("ChatList")
                                    .child(receiverId)
                                    .child(firebaseUser!!.uid)
                            chatListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }
                    })

                }
            }
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(Users::class.java)
                if (notify) {
                    sendNotification(receiverId!!, user!!.username, massage)
                }
                notify = false
            }

        })
    }

    private fun sendNotification(receiverId: String, username: String?, massage: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val token = snapshot.getValue(Token::class.java)
                    val data =
                        Data(
                            firebaseUser!!.uid
                            , R.mipmap.ic_launcher
                            , "$username : $massage"
                            , "new massage",
                            userIdVisit
                        )
                    val sender = Sender(data, token!!.token.toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success != 1) {
                                        Toast.makeText(
                                            this@MassageChatActivity,
                                            "failed nothing happen.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                        })
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 483 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Please wait image is sending....")
            loadingBar.show()

            val imageUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val massageId = ref.push().key
            val imagePath = storageReference.child("$massageId.jpg")

            val uploadTask = imagePath.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation imagePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val massageHashMap = HashMap<String, Any?>()
                    massageHashMap["sender"] = firebaseUser!!.uid
                    massageHashMap["receiver"] = userIdVisit
                    massageHashMap["massage"] = "sent you an image"
                    massageHashMap["isSeen"] = false
                    massageHashMap["url"] = url
                    massageHashMap["massageID"] = massageId

                    ref.child("Chats").child(massageId!!).setValue(massageHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)
                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user = p0.getValue(Users::class.java)
                                        if (notify) {
                                            sendNotification(
                                                userIdVisit!!,
                                                user!!.username,
                                                "sent you an image"
                                            )
                                        }
                                        notify = false
                                    }

                                })
                            }
                        }
                    loadingBar.dismiss()
                }
            }
        }
    }

    private fun retrieveMassages(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if ((chat!!.receiver == senderId && chat.sender == receiverId)
                        || (chat.receiver == receiverId && chat.sender == senderId)
                    ) {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter(
                        this@MassageChatActivity,
                        mChatList as ArrayList<Chat>, receiverImageUrl!!
                    )
                    recyclerView.adapter = chatsAdapter
                }
            }

        })
    }


    private var seenListener: ValueEventListener? = null
    private fun seenMassage(userId: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver == firebaseUser!!.uid && chat.sender.equals(userId)) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isSeen"] = true
                        snapshot.ref.updateChildren(hashMap)

                    }
                }
            }

        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}