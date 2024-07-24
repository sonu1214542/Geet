package com.mvvm.geet2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mvvm.geet2.dao.PlaylistDao
import com.mvvm.geet2.dao.PlaylistSongCrossRefDao
import com.mvvm.geet2.dao.SongDao
import com.mvvm.geet2.entities.Song
import com.mvvm.geet2.entities.Playlist
import com.mvvm.geet2.entities.PlaylistSongCrossRef
import kotlin.concurrent.Volatile

@Database(entities = [Playlist::class,Song::class,PlaylistSongCrossRef::class], version = 1)
abstract class PlaylistDatabase:RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun songDao(): SongDao
    abstract fun playlistSongCrossRefDao(): PlaylistSongCrossRefDao

    companion object{
        @Volatile
        private var Instance:PlaylistDatabase?=null

        fun getDatabase(context: Context) : PlaylistDatabase{
            return Instance ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaylistDatabase::class.java,
                    "playlist_db"
                ).build()
                Instance=instance
                instance
            }
        }
    }
}