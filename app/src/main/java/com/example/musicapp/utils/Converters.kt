package com.example.musicapp.utils

import androidx.room.TypeConverter
import com.example.musicapp.data.model.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<Song> {
        val listType = object : TypeToken<List<Song>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Song>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}