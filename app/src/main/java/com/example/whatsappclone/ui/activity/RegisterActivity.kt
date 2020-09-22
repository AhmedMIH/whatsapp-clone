package com.example.whatsappclone.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.R
import com.example.whatsappclone.ui.AuthListener
import com.example.whatsappclone.ui.viewModel.AuthViewModel
import com.example.whatsappclone.ui.viewModel.AuthViewModelFactory
import kotlinx.android.synthetic.main.activity_register.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RegisterActivity : AppCompatActivity(), AuthListener, KodeinAware {



    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()

    private lateinit var viewModel: AuthViewModel
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
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        viewModel.authListener = this


        register_btn.setOnClickListener {
            viewModel.email = email_register.text.toString()
            viewModel.password = password_register.text.toString()
            viewModel.username = username_register.text.toString()
            viewModel.signUp()
        }
    }

    override fun onStarted() {
       Log.d("auth","loading")
    }

    override fun onSuccess() {
        Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}