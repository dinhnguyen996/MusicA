package com.example.musicapp.di

import android.content.Context
import androidx.room.Room
import com.example.musicapp.data.dao.PlaylistDao
import com.example.musicapp.data.database.AppDatabase
import com.example.musicapp.data.repository.MusicRepository
import com.example.musicapp.data.repository.MusicRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "music_app_database"
        ).build()
    }

    @Provides
    fun providePlaylistDao(database: AppDatabase): PlaylistDao {
        return database.playlistDao()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(@ApplicationContext context: Context): MusicRepository {
        return MusicRepositoryImpl(context, providePlaylistDao(provideAppDatabase(context)))
    }
}