package com.mvvm.geet2.viewadapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.MainActivity
import com.mvvm.geet2.OnItemClickListener
import com.mvvm.geet2.Songs
import com.mvvm.geet2.databinding.ItemSongBinding
import com.mvvm.geet2.helper.ImageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlbumSongAdapter(var songs: MutableList<Songs>,private val itemClickListener : OnItemClickListener) : ListAdapter<Songs, AlbumSongAdapter.ViewHolder>(
    SongsDiffCallback()
) {

    inner class ViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("DefaultLocale")
        fun formatTime(ms: Long): String {
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

        @SuppressLint("SetTextI18n")
        fun bind(song: Songs) {
            binding.songname.text = song.title
            binding.artistname.text = song.artist
            binding.songduration.text = formatTime(song.duration)
            ImageLoader.loadImage(binding.root.context, song.data, binding.songimage)
            binding.position.text=(absoluteAdapterPosition+1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = getItem(position)
        Log.e("ssss",song.title)
        holder.bind(song)
        holder.itemView.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                MainActivity._clickedPos.value=position
                MainActivity.newQueue(songs)
            }
            itemClickListener.onItemClick(song)
        }
    }


    class SongsDiffCallback : DiffUtil.ItemCallback<Songs>() {
        override fun areItemsTheSame(oldItem: Songs, newItem: Songs): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Songs, newItem: Songs): Boolean {
            return oldItem == newItem
        }
    }

    override fun submitList(list: MutableList<Songs>?) {
        songs.clear()
        if (list != null) {
            songs=list
        }
        super.submitList(list)
    }
}
