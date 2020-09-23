package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.data.repositories.UserRepository
import com.example.whatsappclone.ui.AuthListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MassageViewModel(private val repository: UserRepository) : ViewModel() {
    var massage: Chat? = null
    var massageString: String? = null
    var receiverId: String? = null
    var receiverUserName: String? = null
    private var senderId: String = repository.currentUser()!!.uid
    private var senderUsername = repository.getCurrentUserInfo(senderId).value!!.username
    var url: String? = ""
    private val disposables = CompositeDisposable()
    var authListener: AuthListener? = null


    fun deleteMassage() {
        authListener?.onStarted()
        val disposable = repository.deleteMassage(massage!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                authListener?.onSuccess()
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun retrieveUserInformation(): MutableLiveData<Users> {
        return repository.retrieveUserInformation(receiverId!!)
    }

    fun sendMassage() {
        val disposable = repository.sendMassage(senderId, receiverId!!, massageString!!, url!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun retrieveMassage(): MutableLiveData<List<Chat>> {
        return repository.retrieveMassage(senderId, receiverId!!)
    }

    fun seenMassage() {
        val disposable = repository.seenMassage(receiverId!!, senderId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {}
        disposables.add(disposable)
    }

//    fun sendNotification() {
//        val disposable =
//            repository.sendNotification(receiverId!!, senderId, senderUsername, massageString!!)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                }, {
//                })
//        disposables.add(disposable)
//    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}