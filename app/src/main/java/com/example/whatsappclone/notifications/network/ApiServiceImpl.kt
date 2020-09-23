package com.example.whatsappclone.notifications.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiServiceImpl @Inject constructor(private val  apiService: ApiService) {
    fun sendNotification() = Client.getClient("https://fcm.googleapis.com/")?.create(apiService::class.java)
}