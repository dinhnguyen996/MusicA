package com.example.musicapp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicapp.ui.fragment.AlbumFragment
import com.example.musicapp.ui.fragment.ArtistFragment
import com.example.musicapp.ui.fragment.SongFragment
import com.example.musicapp.ui.fragment.PlaylistFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlaylistFragment()
            1 -> ArtistFragment()
            2 -> AlbumFragment()
            3 -> SongFragment()
            else -> Fragment()
        }
    }
}