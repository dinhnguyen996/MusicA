package com.example.musicapp.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.service.MusicService
import com.example.musicapp.ui.adapter.ViewPagerAdapter
import com.example.musicapp.utils.Constant
import com.example.musicapp.utils.GlobalFunctions
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            MusicService.songAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0)
            displayLayoutBottom()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        GlobalFunctions.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.READ_MEDIA_AUDIO
            ), Constant.REQUEST_CODE
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Playlists"
                1 -> "Artists"
                2 -> "Albums"
                3 -> "Songs"
                else -> ""
            }
        }.attach()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mBroadcastReceiver, IntentFilter(Constant.CHANGE_LISTENER)
        )
        initListener()
        displayLayoutBottom()
    }

    private fun displayLayoutBottom() {
        if (MusicService.mediaPlayer == null || Constant.CANCEL_NOTIFICATION == MusicService.songAction) {
            binding.layoutBottom.layoutItem.visibility = View.GONE
            return
        }
        binding.layoutBottom.layoutItem.visibility = View.VISIBLE
        showInfoSongToLayoutBottom()
        showStatusButtonPlay()
    }

    private fun showInfoSongToLayoutBottom() {
        if (MusicService.currentListSong.isEmpty()) {
            return
        }
        val currentSong: Song = MusicService.currentListSong[MusicService.songPosition]
        binding.layoutBottom.tvSongName.text = currentSong.title
        binding.layoutBottom.tvArtist.text = currentSong.artist
        GlobalFunctions.loadAlbumArt(
            applicationContext, binding.layoutBottom.imgSong, currentSong.albumId
        )
    }

    private fun showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            binding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_pause_gray)
        } else {
            binding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_play_gray)
        }
    }

    private fun initListener() {
        binding.layoutBottom.imgPrevious.setOnClickListener {
            clickOnPrevButton()
        }
        binding.layoutBottom.imgPlay.setOnClickListener {
            clickOnPlayButton()
        }
        binding.layoutBottom.imgNext.setOnClickListener {
            clickOnNextButton()
        }
        binding.layoutBottom.imgClose.setOnClickListener {
            clickOnCloseButton()
        }
        binding.layoutBottom.layoutText.setOnClickListener {
            clickOnCloseButton()
        }
        binding.layoutBottom.imgSong.setOnClickListener {
            clickOnCloseButton()
        }
        binding.layoutBottom.tvSongName.setOnClickListener {
            GlobalFunctions.startActivity(
                applicationContext, PlayMusicActivity::class.java
            )
        }
        binding.layoutBottom.tvArtist.setOnClickListener {
            GlobalFunctions.startActivity(
                applicationContext, PlayMusicActivity::class.java
            )
        }
        binding.layoutBottom.imgSong.setOnClickListener {
            GlobalFunctions.startActivity(
                applicationContext, PlayMusicActivity::class.java
            )
        }
    }

    private fun clickOnPrevButton() {
        GlobalFunctions.startMusicService(
            applicationContext, Constant.PREVIOUS, MusicService.songPosition
        )
    }

    private fun clickOnNextButton() {
        GlobalFunctions.startMusicService(
            applicationContext, Constant.NEXT, MusicService.songPosition
        )
    }

    private fun clickOnPlayButton() {
        if (MusicService.mediaPlayer?.isPlaying == true) {
            GlobalFunctions.startMusicService(
                applicationContext, Constant.PAUSE, MusicService.songPosition
            )
        } else {
            GlobalFunctions.startMusicService(
                applicationContext, Constant.RESUME, MusicService.songPosition
            )
        }
    }

    private fun clickOnCloseButton() {
        GlobalFunctions.startMusicService(
            this, Constant.CANCEL_NOTIFICATION, MusicService.songPosition
        )
    }
}