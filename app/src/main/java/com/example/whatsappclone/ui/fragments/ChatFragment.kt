package com.example.whatsappclone.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.UserAdapter
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.ui.AuthListener
import com.example.whatsappclone.ui.viewModel.FragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat.*

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat), AuthListener {

    private val viewModel: FragmentViewModel by viewModels()
    var mUserList = ArrayList<Users>()
    lateinit var progress : ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress = requireView().findViewById(R.id.progressBar)

        viewModel.authListener =this

        viewModel.retrieveChatListChildren().observe(viewLifecycleOwner, Observer { chatList ->
            viewModel.retrieveUsersChildren().observe(viewLifecycleOwner, Observer { users ->
                mUserList.clear()
                users.forEach { user ->
                    chatList.forEach { chat ->
                        if (user.uid == chat.id) {
                            mUserList.add(user)

                        }
                    }
                }
                showRecyclerView(mUserList)
            })
        })
    }

    private fun showRecyclerView(usersList: List<Users>) {
        val recyclerViewChatList: RecyclerView =
            requireView().findViewById(R.id.recycler_view_chatList)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)
        val userAdapter = UserAdapter(requireContext(), (usersList as ArrayList<Users>), true)
        recyclerViewChatList.adapter = userAdapter
    }

    override fun onStarted() {
        progress.visibility = View.VISIBLE
    }

    override fun onSuccess() {
        progress.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}