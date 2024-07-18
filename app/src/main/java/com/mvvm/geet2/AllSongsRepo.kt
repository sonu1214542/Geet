package com.mvvm.geet2

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllSongsRepo (context: Context) {
    companion object {
        var songsLiveData = MutableLiveData<MutableList<Songs>>()
        var songList = ArrayList<Songs>()
        var fetchedd = false
        fun getSongsFlow():Flow<List<Songs>> = songsLiveData.asFlow()
    }


    private val contentResolver: ContentResolver? = context.contentResolver
    private val handler = Handler(context.mainLooper)
    private val audioContentObserver: AudioContentObserver

    init {
        audioContentObserver = AudioContentObserver(context,handler) {
            Log.d("initt", "audio")
        }
        registerContentObserver()
    }

    fun registerContentObserver() {
        contentResolver?.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            audioContentObserver
        )
    }

    fun unregisterContentObserver() {
        contentResolver?.unregisterContentObserver(audioContentObserver)
    }

    fun fetchSongs(): MutableList<Songs> {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.ALBUM,
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} ASC"
            val cursor = contentResolver?.query(uri, projection, selection, null, sortOrder)

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

                val tempList = ArrayList<Songs>()
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val songUriString = it.getString(idColumn) // Assuming idColumn holds the song ID
                    val songUri = Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/" + songUriString)
                    val title = it.getString(titleColumn)
                    val artist = it.getString(artistColumn)
                    val data = it.getString(dataColumn)
                    val duration = it.getLong(durationColumn)
                    val dateAdded = it.getLong(dateAddedColumn)
                    val album = it.getString(albumColumn)

                    tempList.add(Songs(id, title, artist, data, duration, dateAdded,
                        songUri.toString(),album
                    ))
                }
                withContext(Dispatchers.Main) {
                    songList.clear()
                    songList.addAll(tempList)
                    songsLiveData.value = songList
                }
            }
            cursor?.close()
        }
        songsLiveData.postValue(songList.toMutableList())
        fetchedd=true
        return songList
    }
}
