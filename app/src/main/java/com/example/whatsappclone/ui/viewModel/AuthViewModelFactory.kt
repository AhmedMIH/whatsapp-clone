package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.data.repositories.AuthRepository

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }

}