package com.example.musicapp.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.FragmentSongCollectionBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.service.MusicService
import com.example.musicapp.ui.adapter.SongAdapter
import com.example.musicapp.ui.viewmodel.SongViewModel
import com.example.musicapp.utils.Constant
import com.example.musicapp.utils.GlobalFunctions
import dagger.hilt.android.AndroidEntryPoint
import android.app.AlertDialog
import android.view.View

@AndroidEntryPoint
class SongCollectionFragment : DialogFragment(), IOnItemClick {
    private lateinit var binding: FragmentSongCollectionBinding

    private val viewModel: SongViewModel by viewModels()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentSongCollectionBinding.inflate(layoutInflater)

        setupRecyclerView()

        val albumId = arguments?.getLong("albumId", -1L)
        val artistName = arguments?.getString("artistName", "")

        binding.btnBack.setOnClickListener {
            dismiss()
        }

        if (albumId != -1L) {
            if (albumId != null) {
                val albumName = arguments?.getString("albumName", "")
                binding.tvTitle.text = albumName
                viewModel.fetchSongsByAlbum(albumId)
            }
        } else if (artistName != "") {
            if (artistName != null) {
                binding.tvTitle.text = artistName
                viewModel.fetchSongsByArtist(artistName)
            }
        } else {
            viewModel.fetchSongs()
        }

        observeViewModel()

        return AlertDialog.Builder(requireContext()).setView(binding.root).create()
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter(this)
        binding.recyclerSongs.layoutManager = LinearLayoutManager(context)
        binding.recyclerSongs.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerSongs.adapter = songAdapter
    }

    private fun observeViewModel() {
        viewModel.songs.observe(this) { songs ->
            songAdapter.submitList(songs)
        }
    }

    override fun <T> onItemClick(item: T, isLongClick: Boolean, view: View) {
        if (item is Song) {
            if (MusicService.currentListSong.contains(item)) {
                GlobalFunctions.startMusicService(
                    requireContext(), Constant.PLAY, MusicService.currentListSong.indexOf(item)
                )
                return
            }
            MusicService.currentListSong.add(item)
            GlobalFunctions.startMusicService(
                requireContext(), Constant.PLAY, MusicService.currentListSong.size - 1
            )
        }
    }
}