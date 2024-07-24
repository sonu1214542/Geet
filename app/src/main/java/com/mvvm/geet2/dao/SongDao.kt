package com.mvvm.geet2.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mvvm.geet2.entities.Song


@Dao
interface SongDao {
    @Query("SELECT * FROM songs WHERE songId = :songId")
    fun getSongById(songId: Long): Song?

    @Query("""
        SELECT * FROM songs 
        INNER JOIN PlaylistSongCrossRef 
        ON songs.songId = PlaylistSongCrossRef.songId 
        WHERE PlaylistSongCrossRef.playlistId = :playlistId
    """)
    fun getSongsByPlaylistId(playlistId: Long): LiveData<List<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongs(vararg songs: Song)
}