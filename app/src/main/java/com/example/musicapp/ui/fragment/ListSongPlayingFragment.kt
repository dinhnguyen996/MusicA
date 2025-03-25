package com.example.musicapp.ui.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.utils.Constant
import com.example.musicapp.utils.GlobalFunctions
import com.example.musicapp.ui.adapter.ListSongPlayingAdapter
import com.example.musicapp.databinding.FragmentListSongPlayingBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.data.model.Song
import com.example.musicapp.service.MusicService

class ListSongPlayingFragment : Fragment() {
    private lateinit var listSongPlayingAdapter: ListSongPlayingAdapter
    private lateinit var recyclerListSongPlaying: RecyclerView
    private lateinit var fragmentListSongPlayingBinding: FragmentListSongPlayingBinding
    private lateinit var listSong: List<Song>
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateStatusListSongPlaying()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentListSongPlayingBinding =
            FragmentListSongPlayingBinding.inflate(inflater, container, false)
        if (activity != null) {
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
                mBroadcastReceiver, IntentFilter(Constant.CHANGE_LISTENER)
            )
        }

        listSong = MusicService.currentListSong

        renderListSongPlaying()
        updateStatusListSongPlaying()

        fragmentListSongPlayingBinding.btnClear.setOnClickListener {
            MusicService.isPlaying = false
            // delete all song in MusicService.currentListSong except the current song
            MusicService.currentListSong = MusicService.currentListSong.filterIndexed { index, _ ->
                index == MusicService.songPosition
            }.toMutableList()
            renderListSongPlaying()
            updateStatusListSongPlaying()
            updateSongList(MusicService.currentListSong as ArrayList<Song>)
        }

        return fragmentListSongPlayingBinding.root
    }

    private fun renderListSongPlaying() {
        recyclerListSongPlaying = fragmentListSongPlayingBinding.rcvData
        recyclerListSongPlaying.layoutManager = LinearLayoutManager(activity)
        // add divider
        listSongPlayingAdapter = ListSongPlayingAdapter(listSong, object : IOnItemClick {
            override fun <T> onItemClick(item: T, isLongClick: Boolean, view: View) {
                if (item is Song) {
                    playSong(listSong.indexOf(item))
                }
            }
        })
        recyclerListSongPlaying.adapter = listSongPlayingAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateStatusListSongPlaying() {
        for (i in listSong.indices) {
            listSong[i].isPlaying = i == MusicService.songPosition
        }
        listSongPlayingAdapter.notifyDataSetChanged()
    }

    private fun playSong(position: Int) {
        MusicService.isPlaying = false
        GlobalFunctions.startMusicService(requireContext(), Constant.PLAY, position)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSongList(songList: ArrayList<Song>) {
        listSongPlayingAdapter.setSongList(songList)
    }
}