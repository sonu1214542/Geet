package com.mvvm.geet2.viewadapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.R
import com.mvvm.geet2.entities.Playlist
import com.mvvm.geet2.playlistDialogClick

class PlaylistAdapter(private var playlists: List<Playlist>,private val itemClickListener: playlistDialogClick) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playlistName = itemView.findViewById<TextView>(R.id.playlistdialogplaylistname)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistAdapter.PlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaylistAdapter.PlaylistViewHolder, position: Int) {
        val currentPlaylist = playlists[position]
        holder.playlistName.text=currentPlaylist.playlistName
        holder.itemView.setOnClickListener {
            Log.e("playlist name",currentPlaylist.playlistId.toString())
            itemClickListener.onPlaylistClick(currentPlaylist.playlistId)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}

