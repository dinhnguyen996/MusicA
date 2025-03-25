package com.example.musicapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.data.model.Album
import com.example.musicapp.databinding.FragmentAlbumBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.ui.adapter.AlbumAdapter
import com.example.musicapp.ui.viewmodel.AlbumViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumFragment : Fragment(), IOnItemClick {
    private lateinit var binding: FragmentAlbumBinding

    private val viewModel: AlbumViewModel by viewModels()
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter(this)
        binding.recyclerAlbums.layoutManager = LinearLayoutManager(context)
        binding.recyclerAlbums.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerAlbums.adapter = albumAdapter
    }

    private fun observeViewModel() {
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            albumAdapter.submitList(albums)
        }
    }

    override fun <T> onItemClick(item: T, isLongClick: Boolean, view: View) {
        if (item is Album) {
            val songCollectionFragment = SongCollectionFragment()
            val bundle = Bundle()
            bundle.putLong("albumId", item.id)
            bundle.putString("albumName", item.name)
            songCollectionFragment.arguments = bundle
            songCollectionFragment.show(parentFragmentManager, "SongCollectionFragment")
        }
    }
}