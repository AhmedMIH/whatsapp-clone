package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var refUsers: DatabaseReference
    var firebaseUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_Register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        register_btn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val userName = username_register.text.toString()
        val email = email_register.text.toString()
        val password = password_register.text.toString()

        when {
            userName == "" -> {
                Toast.makeText(this, "please write username ", Toast.LENGTH_SHORT).show()
            }
            email == "" -> {
                Toast.makeText(this, "please write email ", Toast.LENGTH_SHORT).show()
            }
            password == "" -> {
                Toast.makeText(this, "please write password ", Toast.LENGTH_SHORT).show()
            }
            else -> {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseUserId = mAuth.currentUser!!.uid
                            refUsers = FirebaseDatabase.getInstance().reference
                                .child("Users")
                                .child(firebaseUserId)

                            val userHashMap = HashMap<String, Any>()
                            userHashMap["uid"] = firebaseUserId
                            userHashMap["username"] = userName
                            userHashMap["profile"] = ""
                            userHashMap["cover"] = ""
                            userHashMap["status"] = "offline"
                            userHashMap["search"] = userName.toLowerCase()
                            userHashMap["facebook"] = ""
                            userHashMap["instagram"] = ""
                            userHashMap["website"] = ""

                            refUsers.updateChildren(userHashMap)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }
                                }

                        } else {
                            Toast.makeText(
                                this,
                                "Error massage  ${task.exception!!.message.toString()}",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }
            }
        }
    }


}