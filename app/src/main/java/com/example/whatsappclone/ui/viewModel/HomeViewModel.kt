package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.data.repositories.AuthRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    val user by lazy {
        repository.currentUser()
    }
    val userLiveData = MutableLiveData<Users>()
    var state: String? = ""
    var childrenName:String? = ""
    private val disposables = CompositeDisposable()


    fun retrieveUserInfo(): MutableLiveData<Users> {
        val disposable = repository.getCurrentUserInfo(user!!.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                userLiveData.postValue(it)
            }
        disposables.add(disposable)
        return userLiveData
    }

    fun updateState(){
        val disposable = repository.updateStatus(state!!,childrenName!!)
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
        disposables.dispose()
    }

}