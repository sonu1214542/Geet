package com.mvvm.geet2.viewadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.R
import com.mvvm.geet2.Songs
import com.mvvm.geet2.databinding.ItemSongBinding
import com.mvvm.geet2.helper.ImageLoader.loadImage
import com.mvvm.geet2.viewadapters.AllSongsAdapter.SongsViewHolder

class QueueAdapter(val songsQueue : MutableLiveData<MutableList<Songs>>) : RecyclerView.Adapter<QueueAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun formatTime(ms: Long): String {
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
        fun bind(song: Songs) {
            loadImage(binding.root.context,song.data,binding.songimage)
            binding.songname.text = song.title
            binding.artistname.text = song.artist
            binding.songduration.text = formatTime(song.duration)
            binding.position.visibility=View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueAdapter.ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QueueAdapter.ViewHolder, position: Int) {
        val song = songsQueue.value?.get(position)
        if (song != null) {
            holder.bind(song)
        }
    }

    override fun getItemCount(): Int {
        return songsQueue.value?.size!!
    }
}