package com.example.musicapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _songs: MutableLiveData<List<Song>> = MutableLiveData()
    val songs: MutableLiveData<List<Song>> get() = _songs

    fun fetchSongs() {
        viewModelScope.launch {
            _songs.value = musicRepository.getAllSongs()
        }
    }

    fun fetchSongsByAlbum(albumId: Long) {
        viewModelScope.launch {
            _songs.value = musicRepository.getSongsByAlbum(albumId)
        }
    }

    fun fetchSongsByArtist(artist: String) {
        viewModelScope.launch {
            _songs.value = musicRepository.getSongsByArtist(artist)
        }
    }

    fun fetchSongsByPlaylist(playlistId: Int) {
        viewModelScope.launch {
            _songs.value = musicRepository.getSongsByPlaylist(playlistId)
        }
    }
}