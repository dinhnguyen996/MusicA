package com.example.musicapp.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.data.model.Playlist
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.FragmentListSongToAddToPlaylistBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.ui.adapter.SongAdapter
import com.example.musicapp.ui.viewmodel.PlaylistViewModel
import com.example.musicapp.ui.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSongToAddToPlaylistFragment : DialogFragment(), IOnItemClick {
    private lateinit var binding: FragmentListSongToAddToPlaylistBinding

    private val songViewModel: SongViewModel by viewModels()
    private val playlistViewModel: PlaylistViewModel by viewModels()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentListSongToAddToPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        songViewModel.fetchSongs()

        observeViewModel()

        binding.btnBack.setOnClickListener {
            dismiss()
        }
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
        songViewModel.songs.observe(viewLifecycleOwner) { songs ->
            songAdapter.submitList(songs)
        }
    }

    override fun <T> onItemClick(item: T, isLongClick: Boolean, view: View) {
        if (item is Song) {
            val playlist = requireArguments().getParcelable<Playlist>("playlist")
            playlist?.let {
                if (it.songs.contains(item)) {
                    Toast.makeText(context, "Song already added", Toast.LENGTH_SHORT).show()
                    return
                }
                it.songs += item
                playlistViewModel.updatePlaylist(it).observe(viewLifecycleOwner) { result ->
                    if (result) {
                        Toast.makeText(context, "Song added successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Failed to add song", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }
}