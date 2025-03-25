package com.example.musicapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicapp.utils.Constant
import com.example.musicapp.utils.GlobalFunctions
import com.example.musicapp.service.MusicService

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.extras!!.getInt(Constant.MUSIC_ACTION)
        GlobalFunctions.startMusicService(context, action, MusicService.songPosition)
    }
}