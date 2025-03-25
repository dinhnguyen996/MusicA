package com.example.musicapp.data.repository

import com.example.musicapp.data.model.Album
import com.example.musicapp.data.model.Playlist
import com.example.musicapp.data.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getAllSongs(): List<Song>
    suspend fun getAllAlbums(): List<Album>
    suspend fun getArtists(): List<String>
    suspend fun getSongsByAlbum(albumId: Long): List<Song>
    suspend fun getSongsByArtist(artist: String): List<Song>
    suspend fun getSongsByPlaylist(playlistId: Int): List<Song>

    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylist(id: Int): Flow<Playlist>
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
}