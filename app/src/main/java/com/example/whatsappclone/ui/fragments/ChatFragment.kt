package com.example.whatsappclone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.UserAdapter
import com.example.whatsappclone.data.model.ChatList
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.notifications.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

class ChatFragment : Fragment() {
    var userAdapter: UserAdapter? = null
    var mUsers: List<Users>? = null
    var userChatList: List<ChatList>? = null
    lateinit var recyclerViewChatList: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerViewChatList = view.findViewById(R.id.recycler_view_chatList)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userChatList = ArrayList()

        addUserToTheList()
        updateToken()

        return view
    }
    private fun addUserToTheList(){
        val ref = FirebaseDatabase.getInstance().reference.child("ChatList")
            .child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (userChatList as ArrayList).clear()
                for (snapshot in p0.children) {
                    val chatList = snapshot.getValue(ChatList::class.java)
                    (userChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

        })

    }

    private fun updateToken() {
        val token = FirebaseInstanceId.getInstance().token
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList() {
        mUsers = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (snapshot in p0.children) {
                    val user = snapshot.getValue(Users::class.java)
                    for (eachChatList in userChatList!!) {
                        if (user!!.uid == eachChatList.id) {
                            (mUsers as ArrayList).add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                recyclerViewChatList.adapter = userAdapter
            }
        })
    }

    override fun onResume() {
        super.onResume()
        retrieveChatList()
    }
}