package com.example.musicapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicapp.ui.adapter.MusicViewPagerAdapter
import com.example.musicapp.databinding.ActivityPlayMusicBinding
import com.example.musicapp.ui.fragment.ListSongPlayingFragment
import com.example.musicapp.ui.fragment.PlaySongFragment

class PlayMusicActivity : AppCompatActivity() {
    private lateinit var activityPlayMusicBinding: ActivityPlayMusicBinding
    private lateinit var musicViewPagerAdapter: MusicViewPagerAdapter
    private val fragments = listOf(
        ListSongPlayingFragment(),
        PlaySongFragment(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPlayMusicBinding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(activityPlayMusicBinding.root)
        musicViewPagerAdapter = MusicViewPagerAdapter(this, fragments)
        activityPlayMusicBinding.viewpager2.adapter = musicViewPagerAdapter
        activityPlayMusicBinding.indicator3.setViewPager(activityPlayMusicBinding.viewpager2)
        activityPlayMusicBinding.viewpager2.currentItem = 1
    }
}