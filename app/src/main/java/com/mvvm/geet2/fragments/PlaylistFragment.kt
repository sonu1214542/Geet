package com.mvvm.geet2.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.mvvm.geet2.PlaylistDatabase
import com.mvvm.geet2.PlaylistViewModelFactory
import com.mvvm.geet2.R
import com.mvvm.geet2.databinding.FragmentPlaylistBinding
import com.mvvm.geet2.entities.Playlist
import com.mvvm.geet2.viewmodels.PlaylistViewModel
import com.mvvm.geet2.repo.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel : PlaylistViewModel by viewModels<PlaylistViewModel> {
        PlaylistViewModelFactory(PlaylistRepository(PlaylistDatabase.getDatabase(requireContext()).playlistDao(),PlaylistDatabase.getDatabase(requireContext()).songDao()))
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }

//    private val viewModel: PlaylistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.addplaylist.setOnClickListener {
//            val newPlaylist = Playlist(playlistName = "New Playlist")
//            viewModel.insertPlaylist(newPlaylist)
//            val newPlaylist2 = Playlist(playlistName = "New Playlist2")
//            viewModel.insertPlaylist(newPlaylist2)
//        }
//        binding.getSongsFromPlaylist.setOnClickListener {
//            val playlistId = 1L
//            lifecycleScope.launch {
//                viewModel.getSongsInPlaylist(playlistId) { songs ->
//                    android.util.Log.e("songs in playlist", songs.toString())
//                }
//            }
//        }

        viewModel.allPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
//            playlists?.let { playlistAdapter.submitList(it) }
            if (playlists!=null){
                Log.e("All Playlists", playlists.toString())
            }
        })

//        binding.getAllplaylistbutton.setOnClickListener{
//            Log.e("all plaaaaaaaaay",viewModel.allPlaylists.value.toString())
//        }
    }
}