package com.example.musicapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.FragmentSongBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.ui.adapter.SongAdapter
import com.example.musicapp.ui.viewmodel.SongViewModel
import com.example.musicapp.utils.GlobalFunctions
import com.example.musicapp.service.MusicService
import com.example.musicapp.ui.activity.PlayMusicActivity
import com.example.musicapp.utils.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongFragment : Fragment(), IOnItemClick {
    private lateinit var binding: FragmentSongBinding

    private val viewModel: SongViewModel by viewModels()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.fetchSongs()

        observeViewModel()
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
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
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
            GlobalFunctions.startActivity(requireContext(), PlayMusicActivity::class.java)
            GlobalFunctions.startMusicService(
                requireContext(), Constant.PLAY, MusicService.currentListSong.size - 1
            )
        }
    }
}