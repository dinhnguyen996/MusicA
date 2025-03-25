package com.example.musicapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.Playlist
import com.example.musicapp.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _playlists: MutableLiveData<List<Playlist>> = MutableLiveData()
    val playlists: MutableLiveData<List<Playlist>> get() = _playlists

    init {
        fetchPlaylists()
    }

    private fun fetchPlaylists() {
        viewModelScope.launch {
            musicRepository.getAllPlaylists().collect { playlists ->
                _playlists.value = playlists
            }
        }
    }

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            musicRepository.insertPlaylist(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                musicRepository.updatePlaylist(playlist)
                result.postValue(true)
            } catch (e: Exception) {
                result.postValue(false)
            }
        }
        return result
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            musicRepository.deletePlaylist(playlist)
        }
    }
}