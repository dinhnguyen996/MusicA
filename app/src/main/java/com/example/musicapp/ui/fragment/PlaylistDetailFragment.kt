package com.example.musicapp.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.R
import com.example.musicapp.data.model.Playlist
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.FragmentPlaylistDetailBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.ui.adapter.SongAdapter
import com.example.musicapp.ui.viewmodel.SongViewModel
import com.example.musicapp.utils.GlobalFunctions
import com.example.musicapp.service.MusicService
import com.example.musicapp.ui.activity.PlayMusicActivity
import com.example.musicapp.ui.viewmodel.PlaylistViewModel
import com.example.musicapp.utils.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailFragment : DialogFragment(), IOnItemClick {
    private lateinit var binding: FragmentPlaylistDetailBinding

    private val songViewModel: SongViewModel by viewModels()
    private val playlistViewModel: PlaylistViewModel by viewModels()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        val playlist = requireArguments().getParcelable<Playlist>("playlist")

        binding.tvTitle.text = playlist?.name

        songViewModel.fetchSongsByPlaylist(playlist!!.id)

        observeViewModel()

        binding.btnBack.setOnClickListener {
            dismiss()
        }

        binding.outlinedButton.setOnClickListener {
            dismiss()
            val bundle = Bundle()
            bundle.putParcelable("playlist", playlist)
            val listSongToAddToPlaylistFragment = ListSongToAddToPlaylistFragment()
            listSongToAddToPlaylistFragment.arguments = bundle
            listSongToAddToPlaylistFragment.show(
                parentFragmentManager, "listSongToAddToPlaylistFragment"
            )
        }

        binding.outlinedButton2.setOnClickListener {
            dismiss()
            MusicService.currentListSong.clear()
            if (playlist.songs.isEmpty()) {
                Toast.makeText(context, "Playlist is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            MusicService.currentListSong.addAll(playlist.songs)
            GlobalFunctions.startMusicService(requireContext(), Constant.PLAY, 0)
            GlobalFunctions.startActivity(requireContext(), PlayMusicActivity::class.java)
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
            if (isLongClick) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_delete_playlist -> {
                            val playlist = requireArguments().getParcelable<Playlist>("playlist")
                            playlist?.let { currentPlaylist ->
                                currentPlaylist.songs -= item
                                playlistViewModel.updatePlaylist(currentPlaylist)
                                    .observe(viewLifecycleOwner) { result ->
                                        if (result) {
                                            Toast.makeText(
                                                context,
                                                "Song deleted successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            songViewModel.fetchSongsByPlaylist(currentPlaylist.id)
                                        } else {
                                            Toast.makeText(
                                                context, "Failed to delete song", Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                            true
                        }
//                    R.id.action_edit_playlist -> {
//                        showEditPlaylistDialog(playlistId)
//                        true
//                    }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }
}