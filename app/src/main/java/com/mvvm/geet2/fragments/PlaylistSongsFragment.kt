package com.mvvm.geet2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mvvm.geet2.R
import com.mvvm.geet2.databinding.FragmentPlaylistSongsBinding
import com.mvvm.geet2.viewadapters.PlaylistSongsAdapter
import com.mvvm.geet2.viewmodels.PlayerViewmodel


class PlaylistSongsFragment : Fragment() {
    private lateinit var binding : FragmentPlaylistSongsBinding
    lateinit var playerViewmodel: PlayerViewmodel
    lateinit var playlistSongsAdapter: PlaylistSongsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding=FragmentPlaylistSongsBinding.inflate(layoutInflater)
//        val view = binding.root
        //setContentView(view)
        playerViewmodel= ViewModelProvider(requireActivity())[PlayerViewmodel::class.java]
        playerViewmodel.initializeMediaController(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentPlaylistSongsBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {

    }
}