package com.example.whatsappclone.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.UserAdapter
import com.example.whatsappclone.modelClass.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    var userAdapter: UserAdapter? = null
    var mUsers: ArrayList<Users>? = null
    private var recyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val search = view.findViewById<EditText>(R.id.search_user)

        recyclerView = view.findViewById(R.id.search_list)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        retrieveAllUser()

        search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUsers(p0.toString().toLowerCase())
            }

        })

        return view
    }

    private fun retrieveAllUser() {

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUser = FirebaseDatabase.getInstance().reference
            .child("Users")

        refUser.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if(search_user.text.toString().isEmpty()){
                    for (snapshot in p0.children) {
                        val user: Users? = snapshot.getValue(Users::class.java)
                        if (!(user!!.uid).equals(firebaseUserID)) {
                            (mUsers as ArrayList<Users>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                    recyclerView!!.adapter = userAdapter
                }

            }

        })

    }

    fun searchForUsers(str: String) {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUser = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("search")
            .startAt(str)
            .endAt(str+"\uf8ff")
        queryUser.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (!(user!!.uid).equals(firebaseUserID)) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

        })
    }
}