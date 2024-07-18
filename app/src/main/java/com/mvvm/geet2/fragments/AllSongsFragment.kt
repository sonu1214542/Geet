package com.mvvm.geet2.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.MainActivity
import com.mvvm.geet2.OnItemClickListener
import com.mvvm.geet2.R
import com.mvvm.geet2.Songs
import com.mvvm.geet2.viewadapters.AllSongsAdapter
import com.mvvm.geet2.viewmodels.AllSongsViewModel
import com.mvvm.geet2.viewmodels.PlayerViewmodel

@UnstableApi
class AllSongsFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var allSongsAdapter: AllSongsAdapter
    lateinit var allSongsviewModel: AllSongsViewModel
    lateinit var progrssbar : ProgressBar
    lateinit var playerViewmodel: PlayerViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        playerViewmodel = ViewModelProvider(requireActivity())[PlayerViewmodel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_all_songs, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progrssbar=view.findViewById(R.id.progressBar2)
        progrssbar.visibility=View.VISIBLE
        recyclerView= view.findViewById(R.id.allsongsrv)
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
        playerViewmodel.initializeMediaController(requireContext())
        allSongsAdapter = AllSongsAdapter(object : OnItemClickListener {
            @OptIn(UnstableApi::class)
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(song: Songs) {
                Toast.makeText(context, song.title, Toast.LENGTH_SHORT).show()
                allSongsviewModel.songsLiveData.observe(requireActivity(), Observer { songs->
                        Log.e("all song frag","changes")
                        MainActivity.clickedSongQueue.postValue(songs)
                })
                playerViewmodel.Play()
            }
        })
        recyclerView.adapter=allSongsAdapter
        playerViewmodel.mediaMetadataLiveData.observe(viewLifecycleOwner, Observer { mediaMetadata ->
            Log.e("meta nameeee",mediaMetadata.title.toString())
        })
        playerViewmodel.playbackState.observe(viewLifecycleOwner, Observer { state->
            Log.e("stateeeeeeeeee",state.toString())
        })
        allSongsviewModel= ViewModelProvider(this)[AllSongsViewModel::class.java]
        allSongsviewModel.songsLiveData.observe(viewLifecycleOwner, Observer { songs ->
            allSongsAdapter.submitList(songs.toMutableList())
            progrssbar.visibility=View.GONE
        })
    }
}

