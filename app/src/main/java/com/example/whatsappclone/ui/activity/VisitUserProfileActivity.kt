package com.example.whatsappclone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.data.model.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)
        val userVisitId = intent.getStringExtra("visit_id")
        var user: Users? = null
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userVisitId!!)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(Users::class.java)
                    username_visit_profile.text = user!!.username
                    Picasso.get().load(user!!.profile).into(profile_visit_profile)
                    Picasso.get().load(user!!.cover).into(cover_visit_profile)
                }
            }
        })
        show_facebook_visit_profile.setOnClickListener {
            if (user!!.facebook != "") {
                val uri = Uri.parse(user!!.facebook)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } else {
                Toast.makeText(this, "this user didn't set a facebook username", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        show_instagram_visit_profile.setOnClickListener {
            if (user!!.instagram != "") {
                val uri = Uri.parse(user!!.instagram)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } else {
                Toast.makeText(this, "this user didn't set a instagram username", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        show_website_visit_profile.setOnClickListener {
            if (user!!.website != "") {
                val uri = Uri.parse(user!!.website)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } else {
                Toast.makeText(this, "this user didn't set a website ", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        send_massage_btn_visit_profile.setOnClickListener {
            val intent = Intent(this, MassageChatActivity::class.java)
            intent.putExtra("visit_id", user!!.uid)
            startActivity(intent)
        }


    }
}