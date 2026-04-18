package com.mesh.app.di

import android.content.Context
import androidx.room.Room
import com.mesh.app.data.local.db.AppDatabase
import com.mesh.app.data.local.db.InProgressDao
import com.mesh.app.data.local.db.MessageDao
import com.mesh.app.data.local.db.PeerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "mesh.db")
            // Prevents crashes when the schema changes during development
            // (e.g. Room version mismatch on reinstall without uninstall)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()
    @Provides fun provideInProgressDao(db: AppDatabase): InProgressDao = db.inProgressDao()
    @Provides fun providePeerDao(db: AppDatabase): PeerDao = db.peerDao()
}
