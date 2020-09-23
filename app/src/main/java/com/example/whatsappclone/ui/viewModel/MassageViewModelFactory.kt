package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.data.repositories.UserRepository


@Suppress("UNCHECKED_CAST")
class MassageViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MassageViewModel(repository) as T
    }

}