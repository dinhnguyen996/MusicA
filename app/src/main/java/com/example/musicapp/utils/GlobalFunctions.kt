package com.example.musicapp.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.receiver.MusicReceiver
import com.example.musicapp.service.MusicService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GlobalFunctions {
    companion object {
        fun startActivity(context: Context, clz: Class<*>?) {
            val intent = Intent(context, clz)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        fun startMusicService(ctx: Context, action: Int, songPosition: Int) {
            val musicService = Intent(ctx, MusicService::class.java)
            musicService.putExtra(Constant.MUSIC_ACTION, action)
            musicService.putExtra(Constant.SONG_POSITION, songPosition)
            ContextCompat.startForegroundService(ctx, musicService)
        }

        fun openMusicReceiver(ctx: Context, action: Int): PendingIntent {
            val intent = Intent(ctx, MusicReceiver::class.java)
            intent.putExtra(Constant.MUSIC_ACTION, action)

            val pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

            return PendingIntent.getBroadcast(ctx.applicationContext, action, intent, pendingFlag)
        }

        fun getCircularBitmap(bitmap: Bitmap): Bitmap {
            // get the bitmap width and height, and determine the radius of the circle
            val width = bitmap.width
            val height = bitmap.height
            val radius = if (width < height) width / 2 else height / 2

            // create a new bitmap with the same width and height as the original, and a transparent color
            val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            canvas.drawColor(Color.TRANSPARENT)

            // create a new paint object to draw the circle
            val paint = Paint()
            paint.isAntiAlias = true
            paint.style = Paint.Style.FILL

            // draw the circle using the paint object
            canvas.drawCircle(width / 2f, height / 2f, radius.toFloat(), paint)

            // use the SRC_IN blend mode to crop the bitmap to the circle
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            // trim all space around the circle
            val newWidth = output.width
            val newHeight = output.height
            var left = newWidth
            var top = newHeight
            var right = 0
            var bottom = 0
            for (x in 0 until newWidth) {
                for (y in 0 until newHeight) {
                    if (output.getPixel(x, y) != Color.TRANSPARENT) {
                        if (x < left) left = x
                        if (x > right) right = x
                        if (y < top) top = y
                        if (y > bottom) bottom = y
                    }
                }
            }
            if (right < left || bottom < top) return output // Empty bitmap
            return Bitmap.createBitmap(output, left, top, right - left + 1, bottom - top + 1)
        }

        fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
            val notGrantedPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
            }

            if (notGrantedPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    activity, notGrantedPermissions.toTypedArray(), requestCode
                )
            }
        }

        fun loadAlbumArt(context: Context, imageView: ImageView, albumId: Long) {
            val sArt = Uri.parse("content://media/external/audio/albumart")
            val uri = ContentUris.withAppendedId(sArt, albumId)
            Glide.with(context).load(uri).error(R.drawable.image_no_available).into(imageView)
        }

        fun getCurrentDateTime(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}