package com.example.whatsappclone.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsappclone.data.model.ChatList
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.data.repositories.ChatsListRepository
import com.example.whatsappclone.ui.AuthListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FragmentViewModel @ViewModelInject constructor(val repository: ChatsListRepository) :
    ViewModel() {
    val user by lazy {
        repository.currentUser()
    }
    var authListener: AuthListener? = null
    private var userList = MutableLiveData<List<Users>>()
    private var searchList = MutableLiveData<List<Users>>()
    private var chatList = MutableLiveData<List<ChatList>>()
    private val disposables = CompositeDisposable()
    var str:String? = ""


    fun retrieveChatListChildren(): MutableLiveData<List<ChatList>> {
        authListener?.onStarted()
        val disposable = repository.retrieveChatListChildren(user!!.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chatList.value = it
            }, {
                authListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
        return chatList
    }

    fun updateToken() {
        repository.updateToken(user!!.uid)
    }

    fun retrieveUsersChildren(): MutableLiveData<List<Users>> {
        authListener?.onStarted()
        val disposable = repository.retrieveUserChildren()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userList.value = it
                authListener?.onSuccess()
            }, {
                authListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
        return userList
    }

    fun searchForUser():MutableLiveData<List<Users>>{
        authListener?.onStarted()
        val disposable = repository.searchForUser(str!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                searchList.value = it
                authListener?.onSuccess()
            }, {
                authListener?.onFailure(it.message.toString())
            })
        disposables.add(disposable)
        return searchList
    }
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}