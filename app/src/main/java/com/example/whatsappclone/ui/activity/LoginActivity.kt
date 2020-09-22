package com.example.whatsappclone.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.R
import com.example.whatsappclone.ui.AuthListener
import com.example.whatsappclone.ui.viewModel.AuthViewModel
import com.example.whatsappclone.ui.viewModel.AuthViewModelFactory
import com.example.whatsappclone.util.startMainActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), AuthListener, KodeinAware {


    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        viewModel.authListener = this
        login_btn.setOnClickListener {
            viewModel.email = email_login.text.toString()
            viewModel.password = password_login.text.toString()
            viewModel.login()
        }
    }

    override fun onStarted() {
        Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess() {
        startMainActivity()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        viewModel.user?.let {
            startMainActivity()
        }
    }
}