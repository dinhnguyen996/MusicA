package com.example.musicapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.musicapp.utils.Converters
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "playlist")
@TypeConverters(Converters::class)
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val timeCreated: String,
    var songs: List<Song>
) : Parcelable
