package com.example.whatsappclone.notifications.network

import com.example.whatsappclone.notifications.MyResponse
import com.example.whatsappclone.notifications.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAqYq0T70:APA91bFlbwAUDvY_Y_5MLG-0uQlqoCESsamzxvcor1MYYtTy92TNBt2Sae2pAOaXT63S9zn1cp-QIoCqI90YNU7_vUQE0RryaR-MfLEME-CqC77N9RK3cJt5yJ5CGvaA6IBVumF4bO5E"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender?): Call<MyResponse>
}