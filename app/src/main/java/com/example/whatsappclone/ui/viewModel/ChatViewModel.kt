package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.data.repositories.UserRepository
import com.example.whatsappclone.ui.AuthListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChatViewModel(private val repository: UserRepository):ViewModel() {
    var massage:Chat? =null
    val disposables = CompositeDisposable()
    var authListener: AuthListener? = null

    fun deleteMassage(){
        authListener?.onStarted()
        val disposable = repository.deleteMassage(massage!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                authListener?.onSuccess()
            },{
                authListener?.onFailure(it.message!!)
            })
    }




    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}