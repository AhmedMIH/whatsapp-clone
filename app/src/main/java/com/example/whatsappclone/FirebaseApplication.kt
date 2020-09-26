package com.example.whatsappclone

import android.app.Application
import com.example.whatsappclone.data.firebase.FirebaseMassageDatabase
import com.example.whatsappclone.data.firebase.FirebaseSource
import com.example.whatsappclone.data.firebase.FirebaseSourceAuth
import com.example.whatsappclone.data.repositories.AuthRepository
import com.example.whatsappclone.data.repositories.BaseRepository
import com.example.whatsappclone.data.repositories.MassageRepository
import com.example.whatsappclone.notifications.network.Client
import com.example.whatsappclone.ui.viewModel.AuthViewModelFactory
import com.example.whatsappclone.ui.viewModel.HomeViewModelFactory
import com.example.whatsappclone.ui.viewModel.MassageViewModelFactory
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

        bind() from singleton { Client }
        bind() from singleton { FirebaseSource() }
        bind() from singleton { FirebaseMassageDatabase(instance()) }
        bind() from singleton { FirebaseSourceAuth() }
        bind() from provider { BaseRepository(instance()) }
        bind() from provider { MassageRepository(instance()) }
        bind() from provider { AuthRepository(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { MassageViewModelFactory(instance()) }

    }
}