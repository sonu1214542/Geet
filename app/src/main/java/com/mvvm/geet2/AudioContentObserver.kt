package com.mvvm.geet2

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import com.mvvm.geet2.helper.ensureBackgroundThread
import java.io.File

class AudioContentObserver(
    private val context: Context,
    handler: Handler,
    private val function: () -> Int
) : ContentObserver(handler) {

    override fun deliverSelfNotifications(): Boolean {
        return super.deliverSelfNotifications()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        ensureBackgroundThread {  if (uri == null) return@ensureBackgroundThread // Handle null URI case
            if (uriExists(context, uri)) {
                val newSong = getSongFromUri(context, uri)
                newSong?.let {
                    val currentSongs = AllSongsRepo.songsLiveData.value ?: mutableListOf()
                    if (currentSongs.none { song -> song.uri == newSong.uri }) {
                        currentSongs.add(newSong)
                        AllSongsRepo.songsLiveData.postValue(currentSongs.toMutableList())
                    }
                }
            } else {
                val songs = AllSongsRepo.songsLiveData.value?.filterNot { it.uri == uri.toString() } ?: return@ensureBackgroundThread
                AllSongsRepo.songsLiveData.postValue(songs.toMutableList())
            } }
    }


    override fun onChange(selfChange: Boolean, uris: MutableCollection<Uri>, flags: Int) {
        super.onChange(selfChange, uris, flags)

        val currentSongs = AllSongsRepo.songsLiveData.value ?: mutableListOf()
        val newAddedSongs = mutableListOf<Songs>()
        val changedUris = uris.toSet()

       ensureBackgroundThread {
           changedUris.forEach { uri ->
               if (uriExists(context, uri)) {
                   getSongFromUri(context, uri)?.let { newSong ->
                       if (currentSongs.none { song -> song.uri == newSong.uri }) {
                           newAddedSongs.add(newSong)
                       }
                   }
               } else {
                   currentSongs.removeAll { it.uri == uri.toString() }
               }
           }

           currentSongs.addAll(newAddedSongs)
           AllSongsRepo.songsLiveData.postValue(currentSongs)
       }
    }

    private fun uriExists(context: Context, uri: Uri?): Boolean {
        if (uri == null) return false
        val filePath = getFilePathFromUri(context, uri)
        return filePath?.let { File(it).exists() } == true
    }

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun getSongFromUri(context: Context, uri: Uri): Songs? {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM
        )
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                return Songs(id, title, artist, data, duration, dateAdded, uri.toString(), album)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }
}
