package com.mvvm.geet2.viewadapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.R
import com.mvvm.geet2.entities.Playlist
import com.mvvm.geet2.playlistDialogClick

class PlaylistDialogAdapter (private var playlist: List<Playlist>,private val itemClickListener: playlistDialogClick): RecyclerView.Adapter<PlaylistDialogAdapter.PlaylistDialogViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistDialogAdapter.PlaylistDialogViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistDialogViewHolder(itemView)
    }

    class PlaylistDialogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val playlistName = itemView.findViewById<TextView>(R.id.playlistdialogplaylistname)
    }

    override fun onBindViewHolder(
        holder: PlaylistDialogAdapter.PlaylistDialogViewHolder,
        position: Int
    ) {
        val currentPlaylist = playlist[position]
        holder.playlistName.text=currentPlaylist.playlistName
        holder.itemView.setOnClickListener {
            Log.e("playlist name",currentPlaylist.playlistId.toString())
            itemClickListener.onPlaylistClick(currentPlaylist.playlistId)
        }
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylist(newPlaylists:List<Playlist>){
        playlist=newPlaylists
        notifyDataSetChanged()
    }

}