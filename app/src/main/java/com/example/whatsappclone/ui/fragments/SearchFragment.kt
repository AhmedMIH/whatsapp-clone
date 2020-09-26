package com.example.whatsappclone.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),AuthListener {

    private val viewModel: FragmentViewModel by viewModels()
    val userList = ArrayList<Users>()
    lateinit var progress : ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress = requireView().findViewById(R.id.progressBarSearch)

        val searchEditText = view.findViewById<EditText>(R.id.search_user)
        viewModel.authListener = this

        viewModel.retrieveUsersChildren().observe(viewLifecycleOwner, Observer {users ->
            userList.clear()
            users.forEach {
                if (it.uid != viewModel.user!!.uid){
                    userList.add(it)
                }
            }
            showRecyclerView()
        })

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUsers(p0.toString())
            }
        })
    }
    fun searchForUsers(str: String) {
        viewModel.str = str.toLowerCase()
        viewModel.searchForUser().observe(viewLifecycleOwner, Observer {users->
            userList.clear()
            users.forEach {
                if (it.uid != viewModel.user!!.uid){
                    userList.add(it)
                }
            }
            showRecyclerView()
        })
    }

    private fun showRecyclerView() {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.search_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val userAdapter = UserAdapter(requireContext(), userList, false)
        recyclerView.adapter = userAdapter
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