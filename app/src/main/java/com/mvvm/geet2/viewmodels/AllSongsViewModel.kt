package com.mvvm.geet2.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mvvm.geet2.AllSongsRepo
import com.mvvm.geet2.Songs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllSongsViewModel(application: Application) :AndroidViewModel(application) {
    private val allSongsRepo = AllSongsRepo(application)
    val songsLiveData: MutableLiveData<MutableList<Songs>> = AllSongsRepo.songsLiveData
    fun registerContentObserver() {
        allSongsRepo.registerContentObserver()
    }
    fun unregisterContentObserver() {
        allSongsRepo.unregisterContentObserver()
    }
    init {
        fetchSongsInBackground()
    }
    fun fetchSongsInBackground() {
        viewModelScope.launch(Dispatchers.IO) {
            allSongsRepo.fetchSongs()
        }
    }
}