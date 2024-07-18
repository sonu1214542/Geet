package com.mvvm.geet2.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.mvvm.geet2.AllSongsRepo
import com.mvvm.geet2.Songs

class AlbumViewModel : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        AllSongsRepo.songsLiveData.removeObserver { songsObserver }
    }

    val albums : MutableLiveData<MutableSet<Pair<String,String>>> = MutableLiveData(mutableSetOf())

    private val songsObserver = Observer<MutableList<Songs>> { songs ->
        val albumSet = mutableSetOf<Pair<String, String>>()
        songs.forEach { song->
            albumSet.add(Pair(song.album,song.data))
        }
        albums.value = albumSet
        Log.e("albums fetch", albums.value.toString())
    }

    init {
        AllSongsRepo.songsLiveData.observeForever(songsObserver)
    }
}