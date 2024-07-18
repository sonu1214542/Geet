package com.mvvm.geet2.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.AllSongsRepo
import com.mvvm.geet2.OnItemClickListener
import com.mvvm.geet2.R
import com.mvvm.geet2.Songs
import com.mvvm.geet2.viewadapters.AlbumSongAdapter
import com.mvvm.geet2.viewmodels.PlayerViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumSongsFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var albumSongAdapter: AlbumSongAdapter
    var songs = mutableListOf<Songs>()
    lateinit var playerViewmodel: PlayerViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        playerViewmodel= ViewModelProvider(requireActivity())[PlayerViewmodel::class.java]
        playerViewmodel.initializeMediaController(requireContext())
        super.onCreate(savedInstanceState)
    }

    private suspend fun filterAlbumSongs(songs: MutableList<Songs>, albumName:String):MutableList<Songs> {
        return songs.filter { it.album==albumName }.toMutableList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("albumsongsfragment transci","albumsongsfragment transci")
        val albumName = arguments?.getString("album_name")
        albumName?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                val filteredSongs = filterAlbumSongs(AllSongsRepo.songsLiveData.value!!, it)
                withContext(Dispatchers.Main) {
                    songs.clear()
                    songs.addAll(filteredSongs)
                    albumSongAdapter.submitList(filteredSongs)
                }
            }
        }
        Log.e("clicked album", albumName.toString())
        return inflater.inflate(R.layout.albumsongs_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albumSongAdapter = AlbumSongAdapter(songs,object : OnItemClickListener {
            override fun onItemClick(song: Songs) {
                playerViewmodel.Play()
            }
        })
        Log.e("AllSongsRepo.songsLiveData.value",songs.size.toString())
        recyclerView=view.findViewById(R.id.album_songs_rv)
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter=albumSongAdapter
    }

    override fun onDestroy() {
        Log.e("albumsongsfragment desrtroyed","albumsongsfragment destroyed")
        super.onDestroy()
    }
}