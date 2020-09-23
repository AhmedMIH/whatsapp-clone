package com.example.whatsappclone.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsappclone.data.model.ChatList
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.data.repositories.UserRepository
import javax.inject.Inject

class chatFragmentViewModel @ViewModelInject constructor(val userRepository: UserRepository):ViewModel() {
    val user by lazy {
        userRepository.currentUser()
    }



    fun retrieveChatListChildren () : MutableLiveData<List<ChatList>> {
        var chatList = userRepository.retrieveChatListChildren(user!!.uid)
        return chatList
    }

    fun retrieveUsersChildren (chatListLiveData:List<ChatList>) : MutableLiveData<List<Users>> {
        var usersList = userRepository.retrieveUserChildren(chatListLiveData)
        return usersList
    }

}