package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.data.repositories.MassageRepository
import com.example.whatsappclone.ui.AuthListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MassageViewModel(private val repository: MassageRepository) : ViewModel() {
    var massage: Chat? = null
    var massageString: String? = ""
    var receiverId: String? = ""
    var receiverUserName: String? = ""
    private var senderId: String = ""
    private var senderUsername = ""
    var url: String? = ""
    private val disposables = CompositeDisposable()
    var authListener: AuthListener? = null

    init {
        senderId = repository.currentUser()!!.uid
    }

    fun deleteMassage() {
        authListener?.onStarted()
        val disposable = repository.deleteMassage(massage!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //authListener?.onSuccess()
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun retrieveUserInformation(): MutableLiveData<Users> {
        return repository.getReceiverUserInfo(receiverId!!)
    }

    fun retrieveSenderUsername() {
        val disposable = repository.getCurrentUserInfo(senderId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                senderUsername = it.username!!
            }
        disposables.add(disposable)
//        val user = repository.getCurrentUserInfo(senderId)
//        senderUsername = user.value!!.username!!
    }

    fun sendMassage() {
        val disposable = repository.sendMassage(senderId, receiverId!!, massageString!!, url!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                authListener?.onSuccess()
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

    fun sendNotification() {
        val disposable =
            repository.sendNotification(receiverId!!, senderId, senderUsername, massageString!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}