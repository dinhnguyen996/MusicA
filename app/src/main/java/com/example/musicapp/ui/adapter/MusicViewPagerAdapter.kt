package com.example.musicapp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MusicViewPagerAdapter(
    fragmentActivity: FragmentActivity?, private val fragments: List<Fragment>
) : FragmentStateAdapter(fragmentActivity!!) {

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}