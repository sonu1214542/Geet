package com.mvvm.geet2.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mvvm.geet2.MainActivity
import com.mvvm.geet2.Songs

class QueueViewModel : ViewModel() {
    val queueList : MutableLiveData<MutableList<Songs>> = MainActivity.clickedSongQueue
}