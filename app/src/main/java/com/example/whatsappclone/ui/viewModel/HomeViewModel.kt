package com.example.whatsappclone.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.data.repositories.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel(
    private val repository: UserRepository
) : ViewModel() {
    val user by lazy {
        repository.currentUser()
    }
    var state: String? = null
    val disposables = CompositeDisposable()


    fun retrieveUserInfo(): MutableLiveData<Users> {
        val userInfoViewModel = repository.retrieveUserInformation()
        return userInfoViewModel
    }

    fun updateState(){
        val disposable = repository.updateStatus(state!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
            })
        disposables.add(disposable)
    }

    fun logout() {
        repository.logout()
    }

    override fun onCleared() {
        super.onCleared()
//        state = ""
//        updateState()
        disposables.dispose()
    }

}