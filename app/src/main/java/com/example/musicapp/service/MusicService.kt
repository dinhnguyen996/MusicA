package com.example.musicapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.utils.Constant
import com.example.musicapp.data.model.Song
import com.example.musicapp.ui.activity.PlayMusicActivity
import com.example.musicapp.utils.GlobalFunctions
import com.example.musicapp.utils.GlobalFunctions.Companion.getCircularBitmap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    companion object {
        var isPlaying = false
        var currentListSong = mutableListOf<Song>()
        var songPosition = 0
        var mediaPlayer: MediaPlayer? = null
        var lengthSong = 0
        var songAction = -1
        var repeatMode = Constant.REPEAT_NONE
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("MusicService", "onStartCommand")
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(Constant.MUSIC_ACTION)) {
                songAction = bundle.getInt(Constant.MUSIC_ACTION)
            }
            if (bundle.containsKey(Constant.SONG_POSITION)) {
                songPosition = bundle.getInt(Constant.SONG_POSITION)
            }

            handleActionMusic(songAction)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
//        when (repeatMode) {
//            Constant.REPEAT_NONE -> {
//                isPlaying = false
//                pauseSong()
//                songAction = Constant.PAUSE
//                GlobalScope.launch { sendMusicNotification() }
//                sendBroadcastChangeListener()
//            }
//
//            Constant.REPEAT_ALL -> {
//                songAction = Constant.NEXT
//                nextSong()
//                GlobalScope.launch { sendMusicNotification() }
//                sendBroadcastChangeListener()
//            }
//
//            Constant.REPEAT_ONE -> {
//                mp.seekTo(0)
//                mp.start()
//                songAction = Constant.PLAY
//                GlobalScope.launch { sendMusicNotification() }
//                sendBroadcastChangeListener()
//            }
//        }
        songAction = Constant.NEXT
        nextSong()
        GlobalScope.launch { sendMusicNotification() }
        sendBroadcastChangeListener()
    }

    override fun onPrepared(mp: MediaPlayer) {
        lengthSong = mediaPlayer!!.duration
        mp.start()
        isPlaying = true
        songAction = Constant.PLAY
        GlobalScope.launch { sendMusicNotification() }
        sendBroadcastChangeListener()
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            Constant.PLAY -> playSong()
            Constant.PREVIOUS -> prevSong()
            Constant.NEXT -> nextSong()
            Constant.PAUSE -> pauseSong()
            Constant.RESUME -> resumeSong()
            Constant.CANCEL_NOTIFICATION -> cancelNotification()
            else -> Unit
        }
    }

    private fun initControl() {
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
    }

    private fun playMediaPlayer(songPath: String) {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(songPath)
            mediaPlayer?.prepareAsync()
            initControl()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playSong() {
        val songPath = currentListSong[songPosition].filePath
        if (songPath.isNotEmpty()) {
            playMediaPlayer(songPath)
            GlobalScope.launch { sendMusicNotification() }
            sendBroadcastChangeListener()
        }
    }

    private fun pauseSong() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPlaying = false
            GlobalScope.launch { sendMusicNotification() }
            sendBroadcastChangeListener()
        }
    }

    private fun resumeSong() {
        if (mediaPlayer != null) {
            mediaPlayer?.start()
            isPlaying = true
            GlobalScope.launch { sendMusicNotification() }
            sendBroadcastChangeListener()
        }
    }

    private fun prevSong() {
        if (repeatMode == Constant.REPEAT_ONE) {
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
            songAction = Constant.PLAY
            GlobalScope.launch { sendMusicNotification() }
            sendBroadcastChangeListener()
        } else {
            if (currentListSong.size > 1) {
                if (songPosition > 0) {
                    songPosition--
                } else {
                    songPosition = currentListSong.size - 1
                }
            } else {
                songPosition = 0
            }
            GlobalScope.launch { sendMusicNotification() }
            sendBroadcastChangeListener()
            playSong()
        }
    }

    private fun nextSong() {
        if (repeatMode == Constant.REPEAT_ONE) {
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
            songAction = Constant.PLAY
            GlobalScope.launch { sendMusicNotification() }
            sendBroadcastChangeListener()
        } else {
            if (currentListSong.size > 1 && songPosition < currentListSong.size - 1) {
                songPosition++
            } else {
                songPosition = 0
            }
            GlobalScope.launch { sendMusicNotification() }
            sendBroadcastChangeListener()
            playSong()
        }
    }

    private fun cancelNotification() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPlaying = false
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        sendBroadcastChangeListener()
        stopSelf()
    }

    @SuppressLint("RemoteViewLayout")
    private suspend fun sendMusicNotification() = withContext(Dispatchers.IO) {
        val song = currentListSong[songPosition]

        val pendingIntent = createPendingIntent()

        val remoteViews = createRemoteViews(R.layout.layout_push_notification_music, song)
        val bigRemoteViews = createRemoteViews(R.layout.layout_push_notification_music_expand, song)

        loadBitmapIntoRemoteViews(remoteViews, bigRemoteViews, song)

        createNotificationChannel()

        handleClickNotification(remoteViews)
        handleClickNotification(bigRemoteViews)

        val notification = NotificationCompat.Builder(applicationContext, "channel_music_player_id")
            .setSmallIcon(R.drawable.ic_small_push_notification).setContentIntent(pendingIntent)
            .setCustomContentView(remoteViews).setCustomBigContentView(bigRemoteViews)
            .setSound(null).build()

        startForeground(1, notification)
    }

    private fun createPendingIntent(): PendingIntent {
        val pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val intent = Intent(applicationContext, PlayMusicActivity::class.java)
        return PendingIntent.getActivity(applicationContext, 0, intent, pendingFlag)
    }

    private fun createRemoteViews(layoutId: Int, song: Song): RemoteViews {
        return RemoteViews(applicationContext.packageName, layoutId).apply {
            setTextViewText(R.id.tv_title, song.title)
            setTextViewText(R.id.textview_artist, song.artist)
        }
    }

    private suspend fun loadBitmapIntoRemoteViews(
        remoteViews: RemoteViews, bigRemoteViews: RemoteViews, song: Song
    ) {
        val bitmap = loadBitmap(song)
        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.img_thumbnail, getCircularBitmap(bitmap))
            bigRemoteViews.setImageViewBitmap(R.id.img_thumbnail, bitmap)
        }
    }

    private fun handleClickNotification(remoteViews: RemoteViews) {
        remoteViews.setOnClickPendingIntent(
            R.id.img_previous, GlobalFunctions.openMusicReceiver(this, Constant.PREVIOUS)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.img_next, GlobalFunctions.openMusicReceiver(this, Constant.NEXT)
        )
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_pause_gray)
            remoteViews.setOnClickPendingIntent(
                R.id.img_play, GlobalFunctions.openMusicReceiver(this, Constant.PAUSE)
            )
        } else {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_play_gray)
            remoteViews.setOnClickPendingIntent(
                R.id.img_play, GlobalFunctions.openMusicReceiver(this, Constant.RESUME)
            )
        }
        remoteViews.setOnClickPendingIntent(
            R.id.img_close, GlobalFunctions.openMusicReceiver(this, Constant.CANCEL_NOTIFICATION)
        )

    }

    private suspend fun loadBitmap(song: Song): Bitmap? {
        return withContext(Dispatchers.IO) {
            val sArt = Uri.parse("content://media/external/audio/albumart")
            val uri = ContentUris.withAppendedId(sArt, song.albumId)
            return@withContext runCatching {
                Glide.with(applicationContext).asBitmap().load(uri).submit(512, 512).get()
            }.getOrElse {
                // Load a default image when the desired image is not available
                BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.image_no_available
                )
            }
        }
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationManagerCompat.from(this).getNotificationChannel("channel_music_player_id")
        if (channel == null) {
            val name = "Music Player"
            val descriptionText = "Music Player Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channelNotification =
                NotificationChannel("channel_music_player_id", name, importance).apply {
                    description = descriptionText
                }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelNotification)
        }
    }

    private fun sendBroadcastChangeListener() {
        // Implicit intent
        val intent = Intent(Constant.CHANGE_LISTENER)
        intent.putExtra(Constant.MUSIC_ACTION, songAction)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}