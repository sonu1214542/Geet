package com.mvvm.geet2.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.R
import com.mvvm.geet2.viewadapters.AlbumAdapters
import com.mvvm.geet2.viewmodels.AlbumViewModel

class AlbumFragment: Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var albumAdapter: AlbumAdapters
    lateinit var albumViewModel: AlbumViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumViewModel = ViewModelProvider(requireActivity())[AlbumViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_albums, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.albumsRecyclerView)
        recyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        albumAdapter = AlbumAdapters(requireContext())
        recyclerView.adapter=albumAdapter
        albumViewModel.albums.observe(viewLifecycleOwner, Observer { albumdata->
            albumAdapter.submitList(albumdata)
        })
    }

    override fun onDestroy() {
        Log.e("AlbumFragment destroyed","AlbumFragment destroyed")
        super.onDestroy()
    }
}