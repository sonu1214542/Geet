package com.mvvm.geet2.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import com.mvvm.geet2.repo.PlaylistRepository
import com.mvvm.geet2.entities.*
import kotlinx.coroutines.Job

class PlaylistViewModel(private val repository: PlaylistRepository) : ViewModel() {

    // Using LiveData for observing data changes
    val allPlaylists: LiveData<List<Playlist>> = repository.getAllPlaylists()

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            repository.insertPlaylist(playlist)
        }
    }

//    private fun loadPlaylists() {
//        viewModelScope.launch {
//            _allPlaylists.value = repository.getAllPlaylists().value
//        }
//    }
//    fun insertPlaylist(playlist: Playlist) = viewModelScope.launch {
//        repository.insertPlaylist(playlist)
//        loadPlaylists()
//    }

    fun updatePlaylist(playlist: Playlist) = viewModelScope.launch {
        repository.updatePlaylist(playlist)
    }

    fun deletePlaylist(playlist: Playlist) = viewModelScope.launch {
        repository.deletePlaylist(playlist)
    }

    fun getPlaylistWithSongs(playlistId: Long): LiveData<PlaylistWithSongs> = liveData {
        emit(repository.getPlaylistWithSongs(playlistId))
    }

    fun insertSongIntoPlaylist(song: Song, playlistId: Long) = viewModelScope.launch {
        repository.insertSongIntoPlaylist(song, playlistId)
    }

    fun deleteSongFromPlaylist(playlistId: Long, songId: Long) = viewModelScope.launch {
        repository.deleteSongFromPlaylist(playlistId, songId)
    }

    fun getSongsInPlaylist(playlistId: Long, callback: (List<Song>) -> Unit) {
        viewModelScope.launch {
            try {
                val songs = repository.getSongsInPlaylist(playlistId)
                callback(songs)
            } catch (e: Exception) {
                // Handle or log the error
                android.util.Log.e("PlaylistViewModel", "Error fetching songs: ${e.message}")
                callback(emptyList())  // Or handle as needed
            }
        }
    }
}
