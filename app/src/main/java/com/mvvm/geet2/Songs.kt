package com.mvvm.geet2

import android.net.Uri
import java.sql.Date

data class Songs(
    val id: Long, val title: String, val artist: String, val data: String, val duration : Long, val dateAdded:Long,val uri:String, val album:String
)

