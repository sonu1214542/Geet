package com.mvvm.geet2.viewadapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.MainActivity
import com.mvvm.geet2.R
import com.mvvm.geet2.databinding.AlbumItemBinding
import com.mvvm.geet2.fragments.AlbumSongsFragment
import com.mvvm.geet2.helper.ImageLoader

class AlbumAdapters(private val context: Context) : RecyclerView.Adapter<AlbumAdapters.AlbumViewHolder>() {

    private val albumData: MutableList<Pair<String, String>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = AlbumItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    inner class AlbumViewHolder(private val binding: AlbumItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(albumInfo: Pair<String,String>) {
            val (name, data) = albumInfo
            ImageLoader.loadImage(context,data,binding.albumfileimage)
            binding.albumfilename.text = name
        }
    }


    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albumData[position])
        holder.itemView.setOnClickListener{
            val AlbumSongsFragment = AlbumSongsFragment()
            Log.e("album click","clicked")
            val args = Bundle()
            args.putString("album_name", albumData[position].first)
            AlbumSongsFragment.arguments=args
            val fragmentManager: FragmentManager = (context as MainActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.albumfragmentholder, AlbumSongsFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return albumData.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newAlbumData: MutableSet<Pair<String, String>>) {
        albumData.clear()
        albumData.addAll(newAlbumData)
        notifyDataSetChanged()
    }
}
