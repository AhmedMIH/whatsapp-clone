package com.example.whatsappclone

import android.app.Application
import com.example.whatsappclone.data.firebase.FirebaseSource
import com.example.whatsappclone.data.repositories.UserRepository
import com.example.whatsappclone.notifications.network.ApiService
import com.example.whatsappclone.notifications.network.ApiServiceImpl
import com.example.whatsappclone.notifications.network.Client
import com.example.whatsappclone.ui.viewModel.AuthViewModelFactory
import com.example.whatsappclone.ui.viewModel.MassageViewModelFactory
import com.example.whatsappclone.ui.viewModel.HomeViewModelFactory
import dagger.hilt.android.HiltAndroidApp
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

@HiltAndroidApp
class FirebaseApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@FirebaseApplication))

//        bind() from singleton { Client }
//     //   bind() from singleton { ApiService() }
//        bind() from singleton { ApiServiceImpl(instance()) }
        bind() from singleton { FirebaseSource() }
        bind() from singleton { UserRepository(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { MassageViewModelFactory(instance()) }

    }
}