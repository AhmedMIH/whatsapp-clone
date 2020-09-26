package com.example.whatsappclone.di

import com.example.whatsappclone.data.firebase.FirebaseRealTimeDb
import com.example.whatsappclone.data.firebase.FirebaseSetting
import com.example.whatsappclone.data.firebase.FirebaseSource
import com.example.whatsappclone.data.firebase.FirebaseSourceAuth
import com.example.whatsappclone.data.repositories.AuthRepository
import com.example.whatsappclone.data.repositories.BaseRepository
import com.example.whatsappclone.data.repositories.ChatsListRepository
import com.example.whatsappclone.data.repositories.SettingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseSource() = FirebaseSource()

    @Provides
    @Singleton
    fun provideFirebaseRealTimeDb() = FirebaseRealTimeDb()

    @Provides
    @Singleton
    fun provideFirebaseSetting() = FirebaseSetting()



    @Provides
    @Singleton
    fun provideBaseRepository(firebaseSource: FirebaseSource) = BaseRepository(firebaseSource)


    @Provides
    @Singleton
    fun provideChatsListRepository(firebaseSource: FirebaseRealTimeDb) = ChatsListRepository(firebaseSource)


    @Provides
    @Singleton
    fun provideSettingRepository(firebaseSource: FirebaseSetting) = SettingRepository(firebaseSource)


}