package com.example.whatsappclone.di

import com.example.whatsappclone.data.firebase.FirebaseSource
import com.example.whatsappclone.data.repositories.UserRepository
import com.example.whatsappclone.notifications.network.ApiService
import com.example.whatsappclone.notifications.network.ApiServiceImpl
import com.example.whatsappclone.notifications.network.Client
import com.google.android.gms.common.api.Api
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseSource() = FirebaseSource()

    @Provides
    @Singleton
    fun provideUserRepository(firebaseSource: FirebaseSource) = UserRepository(firebaseSource)

}