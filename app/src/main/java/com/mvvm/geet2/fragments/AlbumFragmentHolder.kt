package com.mvvm.geet2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mvvm.geet2.R

class AlbumFragmentHolder : Fragment(R.layout.album_albumsongs_fragment_holder) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.album_albumsongs_fragment_holder,container,false)
    }
}