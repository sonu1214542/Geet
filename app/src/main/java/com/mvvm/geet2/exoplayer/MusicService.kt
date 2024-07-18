package com.mvvm.geet2.exoplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mvvm.geet2.MainActivity
import com.mvvm.geet2.R


@UnstableApi
class MusicService : Service() {
    companion object{
        const val NOTIFICATION_ID=1
        const val NOTIFICATION_CHANNEL="notification_channel"
    }

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerNotificationManager : PlayerNotificationManager
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = NOTIFICATION_CHANNEL
            val channelName = NOTIFICATION_CHANNEL
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Set up PlayerNotificationManager
        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            NOTIFICATION_ID,
            NOTIFICATION_ID.toString()
        ).apply {
            setMediaDescriptionAdapter(MediaDescriptionAdapter())
            setNotificationListener(notificationListener)
        }.build().apply {
            setMediaSessionToken(mediaSession.sessionCompatToken)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setUseChronometer(true)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
        }
    }

    private val notificationListener = object : PlayerNotificationManager.NotificationListener{
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            super.onNotificationCancelled(notificationId, dismissedByUser)
        }

        override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
            super.onNotificationPosted(notificationId, notification, ongoing)
            if (ongoing) {
                startForeground(notificationId, notification)
            } else {
                stopForeground(false)
            }
        }
    }

    private inner class MediaDescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return  mediaSession.player.mediaMetadata.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val intent = Intent(this@MusicService,MainActivity::class.java)
            return PendingIntent.getActivity(this@MusicService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaSession.player.mediaMetadata.artist
        }

        override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
            Glide.with(this@MusicService).asBitmap().load(player.mediaMetadata.artworkUri).into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback.onBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                   Unit
                }
            })
            return null
        }

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}

