package com.example.musicapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _artists: MutableLiveData<List<String>> = MutableLiveData()
    val artists: MutableLiveData<List<String>> get() = _artists

    init {
        fetchArtists()
    }

    private fun fetchArtists() {
        viewModelScope.launch {
            _artists.value = musicRepository.getArtists()
        }
    }
}