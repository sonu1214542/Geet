package com.mvvm.geet2.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mvvm.geet2.entities.PlaylistSongCrossRef
import com.mvvm.geet2.entities.Song

@Dao
interface PlaylistSongCrossRefDao {
    // Insert a song into a playlist
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongIntoPlaylist(crossRef: PlaylistSongCrossRef)

    // Delete a song from a playlist
    @Delete
    suspend fun deleteSongFromPlaylist(crossRef: PlaylistSongCrossRef)

    // Get all songs in a playlist
    @Transaction
    @Query("SELECT * FROM songs INNER JOIN PlaylistSongCrossRef ON songs.songId = PlaylistSongCrossRef.songId WHERE PlaylistSongCrossRef.playlistId = :playlistId")
    suspend fun getSongsInPlaylist(playlistId: Long): List<Song>
}