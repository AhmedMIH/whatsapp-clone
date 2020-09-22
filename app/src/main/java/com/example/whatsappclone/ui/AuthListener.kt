package com.example.whatsappclone.ui

interface AuthListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}