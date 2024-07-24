package com.mvvm.geet2.viewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.MainActivity
import com.mvvm.geet2.OnItemClickListener
import com.mvvm.geet2.Songs
import com.mvvm.geet2.databinding.ItemSongBinding
import com.mvvm.geet2.helper.ImageLoader.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AllSongsAdapter(private val itemClickListener: OnItemClickListener) : ListAdapter<Songs, AllSongsAdapter.SongsViewHolder>(
    SongsDiffCallback()
) {
    override fun submitList(list: MutableList<Songs>?) {
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        val song = getItem(position)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(song)
            CoroutineScope(Dispatchers.Default).launch {
                MainActivity._clickedPos.value=position
            }
        }
        holder.itemView.setOnLongClickListener {
            itemClickListener.onItemLongClick(song)
            true
        }
        holder.bind(song)
    }


    class SongsViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun formatTime(ms: Long): String {
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
        fun bind(song: Songs) {
            val songDuaration = formatTime(song.duration)
            loadImage(binding.root.context,song.data,binding.songimage)
            binding.songname.text = song.title
            binding.artistname.text = song.artist
            binding.songduration.text = songDuaration
            binding.position.text=(absoluteAdapterPosition+1).toString()
        }
    }

    class SongsDiffCallback : DiffUtil.ItemCallback<Songs>() {
        override fun getChangePayload(oldItem: Songs, newItem: Songs): Any? {
            return super.getChangePayload(oldItem, newItem)
        }

        override fun areItemsTheSame(oldItem: Songs, newItem: Songs): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Songs, newItem: Songs): Boolean {
            return oldItem == newItem
        }
    }
}




