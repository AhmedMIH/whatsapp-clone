package com.example.whatsappclone.data.repositories

import com.example.whatsappclone.data.firebase.FirebaseSetting

class SettingRepository(private val firebaseSetting: FirebaseSetting) : BaseRepository(firebaseSetting) {
    fun updateChildren(child:String,childrenName:String) = firebaseSetting.updateChildren(child, childrenName)
}