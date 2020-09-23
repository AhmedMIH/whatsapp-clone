package com.example.whatsappclone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.UserAdapter
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.notifications.Token
import com.example.whatsappclone.ui.viewModel.chatFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {
//    var userAdapter: UserAdapter? = null

    private var firebaseUser: FirebaseUser? = null

    private val viewModel: chatFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)



        viewModel.retrieveChatListChildren().observe(viewLifecycleOwner, Observer { chatList ->
            viewModel.retrieveUsersChildren(chatList)
                .observe(viewLifecycleOwner, Observer { usersList ->
                    showRecyclerView(usersList)
                })
        })
        //   updateToken()

        return view
    }

    private fun updateToken() {
        val token = FirebaseInstanceId.getInstance().token
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    fun showRecyclerView(usersList: List<Users>) {
        val recyclerViewChatList: RecyclerView =
            requireView().findViewById(R.id.recycler_view_chatList)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val userAdapter = UserAdapter(requireContext(), (usersList as ArrayList<Users>), true)
        recyclerViewChatList.adapter = userAdapter
    }

    override fun onResume() {
        super.onResume()
        //retrieveChatList()
    }
}