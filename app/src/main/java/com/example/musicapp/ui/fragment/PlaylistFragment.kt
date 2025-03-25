package com.example.musicapp.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.R
import com.example.musicapp.data.model.Playlist
import com.example.musicapp.databinding.FragmentPlaylistBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.ui.adapter.PlaylistAdapter
import com.example.musicapp.ui.viewmodel.PlaylistViewModel
import com.example.musicapp.utils.GlobalFunctions
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : Fragment(), IOnItemClick {
    private lateinit var binding: FragmentPlaylistBinding

    private val viewModel: PlaylistViewModel by viewModels()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        binding.btnCreatePlaylist.setOnClickListener {
            showCreatePlaylistDialog()
        }
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter(this)
        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistRecyclerView.adapter = playlistAdapter
    }

    private fun observeViewModel() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.submitList(playlists)
        }
    }

    private fun showCreatePlaylistDialog() {
        val dialogCreatePlaylist = Dialog(requireContext())
        dialogCreatePlaylist.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogCreatePlaylist.setContentView(R.layout.dialog_create_playlist)
        val window = dialogCreatePlaylist.window ?: return
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnCreate = dialogCreatePlaylist.findViewById<Button>(R.id.btn_create_playlist)
        val btnCancel = dialogCreatePlaylist.findViewById<Button>(R.id.btn_cancel)
        btnCreate.setOnClickListener {
            val playlistName =
                dialogCreatePlaylist.findViewById<TextInputEditText>(R.id.et_playlist_name).text.toString()
            if (playlistName.isNotEmpty()) {
                val playlist =
                    Playlist(0, playlistName, GlobalFunctions.getCurrentDateTime(), emptyList())
                viewModel.insertPlaylist(playlist)
                dialogCreatePlaylist.dismiss()
            } else {
                Toast.makeText(
                    requireContext(), "Playlist name cannot be empty", Toast.LENGTH_SHORT
                ).show()
            }
        }
        btnCancel.setOnClickListener {
            dialogCreatePlaylist.dismiss()
        }
        dialogCreatePlaylist.show()
    }

    override fun <T> onItemClick(item: T, isLongClick: Boolean, view: View) {
        if (item is Playlist) {
            if (isLongClick) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_delete_playlist -> {
                            viewModel.deletePlaylist(item)
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
            } else {
                val playlistDetailFragment = PlaylistDetailFragment()
                val bundle = Bundle()
                bundle.putParcelable("playlist", item)
                playlistDetailFragment.arguments = bundle
                playlistDetailFragment.show(parentFragmentManager, "playlist_detail")
            }
        }
    }
}