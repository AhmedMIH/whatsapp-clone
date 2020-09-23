package com.example.whatsappclone.util

import android.content.Context
import android.content.Intent
import com.example.whatsappclone.ui.activity.LoginActivity
import com.example.whatsappclone.ui.activity.MainActivity
import com.example.whatsappclone.ui.activity.VisitUserProfileActivity

fun Context.startMainActivity() =
    Intent(this, MainActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.startLoginActivity() =
    Intent(this, LoginActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.startVisitUserProfileActivity(userIdVisit: String) =
    Intent(this, VisitUserProfileActivity::class.java).also {
        it.putExtra("visit_id", userIdVisit)
        startActivity(it)
    }