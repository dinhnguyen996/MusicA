package com.example.musicapp.data.model

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val songCount: Int,
    val firstYear: Int,
)