package com.mvvm.geet2.viewmodels

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.mvvm.geet2.MusicService

class PlayerViewmodel : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable{
        override fun run() {
            val currentPosition = mediaController.currentPosition
            _positionLiveData.postValue(currentPosition)
            handler.postDelayed(this,1000)
        }
    }


    fun startUpdatingCurrentPosition() {
        handler.post(updateProgressRunnable)
    }

    // Stop updating the current position
    fun stopUpdatingCurrentPosition() {
        handler.removeCallbacks(updateProgressRunnable)
    }

    private val _playbackState = MutableLiveData<Int>()
    val playbackState: LiveData<Int> get() = _playbackState

    private val _mediaControllerLiveData = MutableLiveData<MediaController>()
    val mediaControllerLiveData: LiveData<MediaController> get() = _mediaControllerLiveData

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlayingLiveData : LiveData<Boolean> get() = _isPlaying

    private val _mediaMetadataLiveData = MutableLiveData<MediaMetadata>()
    val mediaMetadataLiveData: LiveData<MediaMetadata> get() = _mediaMetadataLiveData

    private val _positionLiveData = MutableLiveData<Long>()
    val positionLiveData: LiveData<Long> get() = _positionLiveData

    private val _durationLiveData = MutableLiveData<Long>()
    val durationLiveData: LiveData<Long> get() = _durationLiveData

    private lateinit var mediaController: MediaController


    fun initializeMediaController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        val mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            val mediaController = mediaControllerFuture.get()
            setMediaController(mediaController)
            _mediaControllerLiveData.postValue(mediaController)
        }, MoreExecutors.directExecutor())
    }

    @OptIn(UnstableApi::class)
    fun setMediaController(  controller: MediaController){
        mediaController = controller
        mediaController.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                _playbackState.postValue(playbackState)
                super.onPlaybackStateChanged(playbackState)
            }
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                mediaMetadata.let {
                    _mediaMetadataLiveData.postValue(it)
                }
                _durationLiveData.postValue(mediaController.duration)
                super.onMediaMetadataChanged(mediaMetadata)
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.postValue(isPlaying)
                super.onIsPlayingChanged(isPlaying)
            }
        })
    }


    fun Play() {
        if (this::mediaController.isInitialized) {
            mediaController.play()
                startUpdatingCurrentPosition()
        }
    }

    fun moveMediaItem(currentIndex: Int, newIndex: Int) {
        if (this::mediaController.isInitialized) {
            mediaController.moveMediaItems(currentIndex, currentIndex + 1, newIndex)
        }
    }

    fun Pause(){
        mediaController.pause()
        stopUpdatingCurrentPosition()
    }

    fun PlayNext(){
        mediaController.seekToNextMediaItem()
    }

    fun PlayPrev(){
        mediaController.seekToPreviousMediaItem()
    }

    fun SeekTo(pos:Long){
        mediaController.seekTo(pos)
    }

    override fun onCleared() {
        mediaController.release()
        super.onCleared()
    }
}