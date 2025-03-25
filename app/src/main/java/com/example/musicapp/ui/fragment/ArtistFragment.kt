package com.example.musicapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.FragmentArtistBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.ui.adapter.ArtistAdapter
import com.example.musicapp.ui.viewmodel.ArtistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistFragment : Fragment(), IOnItemClick {
    private lateinit var binding: FragmentArtistBinding

    private val viewModel: ArtistViewModel by viewModels()
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        artistAdapter = ArtistAdapter(this)
        binding.artistRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.artistRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL
            )
        )
        binding.artistRecyclerView.adapter = artistAdapter
    }

    private fun observeViewModel() {
        viewModel.artists.observe(viewLifecycleOwner) { artists ->
            artistAdapter.submitList(artists)
        }
    }

    override fun <T> onItemClick(item: T, isLongClick: Boolean, view: View) {
        if (item is String) {
            val songCollectionFragment = SongCollectionFragment()
            val bundle = Bundle()
            bundle.putString("artistName", item)
            songCollectionFragment.arguments = bundle
            songCollectionFragment.show(parentFragmentManager, "SongCollectionFragment")
        }
    }
}