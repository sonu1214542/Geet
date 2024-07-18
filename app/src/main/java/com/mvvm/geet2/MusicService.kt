package com.mvvm.geet2


import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.mvvm.geet2.MainActivity.Companion.clickedSongQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicService : MediaSessionService() {

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("MusicService", "Service removed")
        stopForeground(true)
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        clickedSongQueue.removeObserver(songObserver)
        mediaSession.release()
        exoPlayer.release()
        super.onDestroy()
    }

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).setHandleAudioBecomingNoisy(true).build()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()
        exoPlayer.setAudioAttributes(audioAttributes, true)

        clickedSongQueue.observeForever(songObserver)

//            clickedSongQueue.observeForever { songs ->
//                Log.e("music serrrrrr ", songs.toString())
//
//                val mediaItems = songs.map { song ->
//                    MediaItem.Builder()
//                        .setUri(song.uri)
//                        .setMediaId(song.id.toString())
//                        .build()
//                }
//
//                    exoPlayer.setMediaItems(mediaItems)
//                    exoPlayer.prepare()
//            }

// Coroutine for collecting clicked position
        CoroutineScope(Dispatchers.Default).launch {
            MainActivity.clickedPos.collect { pos ->
                withContext(Dispatchers.Main) {
                    exoPlayer.seekTo(pos, C.TIME_UNSET)
                }
            }
        }
    }

    private val songObserver = { songs: List<Songs> ->
        Log.e("MusicService", songs.toString())

        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setUri(song.uri)
                .setMediaId(song.id.toString())
                .build()
        }

        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

}
