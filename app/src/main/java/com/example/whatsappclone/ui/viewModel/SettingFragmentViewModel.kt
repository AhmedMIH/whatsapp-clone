package com.example.whatsappclone.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.data.repositories.SettingRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SettingFragmentViewModel  @ViewModelInject constructor(val repository: SettingRepository) :ViewModel() {
    val disposables = CompositeDisposable()
    val userLiveData = MutableLiveData<Users>()

    var child: String? = ""
    var childrenName:String? = ""
    val user by lazy {
        repository.currentUser()
    }

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
    fun updateChildren(){
        val disposable = repository.updateChildren(child!!,childrenName!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
            })
        disposables.add(disposable)
    }
}