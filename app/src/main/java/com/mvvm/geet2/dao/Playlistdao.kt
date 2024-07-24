package com.mvvm.geet2.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mvvm.geet2.entities.Playlist
import com.mvvm.geet2.entities.PlaylistSongCrossRef
import com.mvvm.geet2.entities.PlaylistWithSongs
import com.mvvm.geet2.entities.Song


@Dao
interface PlaylistDao {

    // Insert a new playlist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    // Update an existing playlist
    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    // Delete a playlist
    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    // Get a playlist with its songs
    @Transaction
    @Query("SELECT * FROM Playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylistWithSongs(playlistId: Long): PlaylistWithSongs

    // Get all playlists
    @Query("SELECT * FROM Playlists")
    fun getAllPlaylists(): LiveData<List<Playlist>>

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
