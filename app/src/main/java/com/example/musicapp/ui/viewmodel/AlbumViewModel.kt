package com.example.musicapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.Album
import com.example.musicapp.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _albums: MutableLiveData<List<Album>> = MutableLiveData()
    val albums: MutableLiveData<List<Album>> get() = _albums

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            _albums.value = musicRepository.getAllAlbums()
        }
    }
}