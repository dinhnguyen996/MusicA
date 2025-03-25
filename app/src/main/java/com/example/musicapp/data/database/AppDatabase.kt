package com.example.musicapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicapp.data.dao.PlaylistDao
import com.example.musicapp.data.model.Playlist
import com.example.musicapp.utils.Converters

@Database(entities = [Playlist::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}