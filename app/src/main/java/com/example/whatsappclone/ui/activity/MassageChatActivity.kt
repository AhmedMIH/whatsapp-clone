package com.example.whatsappclone.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.ChatsAdapter
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.notifications.*
import com.example.whatsappclone.ui.AuthListener
import com.example.whatsappclone.ui.ClickListener
import com.example.whatsappclone.ui.fragments.ApiService
import com.example.whatsappclone.ui.viewModel.ChatViewModel
import com.example.whatsappclone.ui.viewModel.ChatViewModelFactory
import com.example.whatsappclone.util.startVisitUserProfileActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_massage_chat.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MassageChatActivity : AppCompatActivity(), AuthListener, KodeinAware {
    var userIdVisit: String? = ""
    private var userVisitProfile: String? = ""
    var firebaseUser: FirebaseUser? = null
    private var chatsAdapter: ChatsAdapter? = null
    private var mChatList: List<Chat>? = null
    private lateinit var recyclerView: RecyclerView

    private var notify = false
    var apiService: ApiService? = null

    override val kodein by kodein()
    private val factory: ChatViewModelFactory by instance()
    private lateinit var viewModel: ChatViewModel

    private val listener = object : ClickListener {
        override fun deleteMassage(massage: Chat) {
            viewModel.massage = massage
            viewModel.deleteMassage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_massage_chat)
        apiService = Client.getClient("https://fcm.googleapis.com/")!!
            .create(ApiService::class.java)

        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]
        viewModel.authListener = this

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
            startVisitUserProfileActivity(userIdVisit!!)
        }
        username_massage_chat.setOnClickListener {
            startVisitUserProfileActivity(userIdVisit!!)
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerView = findViewById(R.id.recycler_view_massage_chat)
        recyclerView.setHasFixedSize(true)
        val linerLayoutManager = LinearLayoutManager(this)
        linerLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linerLayoutManager

        viewModel.receiverId = userIdVisit
        viewModel.retrieveUserInformation().observe(this, Observer { user ->
            username_massage_chat.text = user!!.username
            viewModel.receiverUserName = user.username
            userVisitProfile = user.profile
            if (user.profile == "") {
                Picasso.get().load(R.drawable.ic_profile).into(profile_image_massage_chat)
            } else
                Picasso.get().load(user.profile).into(profile_image_massage_chat)
            viewModel.retrieveMassage().observe(this, Observer {
                mChatList = it
                chatsAdapter = ChatsAdapter(
                    this@MassageChatActivity,
                    mChatList as ArrayList<Chat>, userVisitProfile!!,
                    listener
                )
                recyclerView.adapter = chatsAdapter
            })

        })
        send_massage_btn.setOnClickListener {
            notify = true
            val massage = text_massage.text.toString()
            if (massage == "") {
                Toast.makeText(this, "please write a massage first  ", Toast.LENGTH_LONG).show()
            } else {
                viewModel.massageString = massage
                viewModel.url = ""
                viewModel.sendMassage()
                sendNotification(userIdVisit!!, viewModel.receiverUserName, massage)
                notify = false
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

        viewModel.seenMassage()
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
                            firebaseUser!!.uid,
                            R.mipmap.ic_launcher,
                            "$username : $massage",
                            "new massage",
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
                    viewModel.url = url
                    viewModel.massageString = "sent you an image"
                    viewModel.sendMassage()
                    loadingBar.dismiss()
                }
            }
        }
    }

    override fun onStarted() {
        Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess() {
        Toast.makeText(this, "delete massage successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, "error $message", Toast.LENGTH_SHORT).show()
    }
}