package com.mvvm.geet2.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "Playlists",
    indices = [Index(value = ["playlistName"], unique = true)])
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    val playlistName:String
)


@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true) val songId: Long = 0,
    val songName:String,
    val artistName:String,
    val albumName:String,
    val songPath:String,
    val duration:Long,
    val dateAdded:Long,
    val uri:String,
)

@Entity(primaryKeys = ["playlistId","songId"])
data class PlaylistSongCrossRef(
    val playlistId:Long,
    val songId:Long
)

data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "songId",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<Song>
)

data class SongWithPlaylists(
    @Embedded val song: Song,
    @Relation(
        parentColumn = "songId",
        entityColumn = "playlistId",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val playlists: List<Playlist>
)
