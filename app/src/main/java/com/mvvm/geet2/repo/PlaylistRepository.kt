package com.mvvm.geet2.repo

import androidx.lifecycle.LiveData
import com.mvvm.geet2.dao.PlaylistDao
import com.mvvm.geet2.dao.SongDao
import com.mvvm.geet2.entities.Playlist
import com.mvvm.geet2.entities.PlaylistSongCrossRef
import com.mvvm.geet2.entities.PlaylistWithSongs
import com.mvvm.geet2.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepository(private val playlistDao: PlaylistDao,private val songDao: SongDao) {

    suspend fun insertPlaylist(playlist: Playlist): Long {
        return playlistDao.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist)
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }

    suspend fun getPlaylistWithSongs(playlistId: Long): PlaylistWithSongs {
        return playlistDao.getPlaylistWithSongs(playlistId)
    }

    fun getAllPlaylists(): LiveData<List<Playlist>> {
        return playlistDao.getAllPlaylists()
    }

    suspend fun insertSongIntoPlaylist(song: Song,playlistId: Long) {
       withContext(Dispatchers.IO){
           songDao.insertSongs(song)
           val crossRef = PlaylistSongCrossRef(playlistId, song.songId)
           playlistDao.insertSongIntoPlaylist(crossRef)
       }
    }

    suspend fun deleteSongFromPlaylist(playlistId: Long, songId: Long) {
        val crossRef = PlaylistSongCrossRef(playlistId, songId)
        playlistDao.deleteSongFromPlaylist(crossRef)
    }

    suspend fun getSongsInPlaylist(playlistId: Long): List<Song> {
        return playlistDao.getSongsInPlaylist(playlistId)
    }

}