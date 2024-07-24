package com.mvvm.geet2

interface OnItemClickListener {
    fun onItemClick(song: Songs)
    fun onItemLongClick(song: Songs)
}

interface playlistDialogClick{
    fun onPlaylistClick(playlistId:Long)
}

