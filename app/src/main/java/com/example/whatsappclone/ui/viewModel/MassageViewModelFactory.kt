package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappclone.data.repositories.AuthRepository
import com.example.whatsappclone.data.repositories.MassageRepository


@Suppress("UNCHECKED_CAST")
class MassageViewModelFactory(
    private val repository: MassageRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MassageViewModel(repository) as T
    }

}