package com.mvvm.geet2.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mvvm.geet2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        Thread {
            callback()
        }.start()
    } else {
        callback()
    }
}


object ImageLoader {

    fun loadImage(context: Context, filePath: String, imageView: ImageView) {
        CoroutineScope(Dispatchers.IO).launch {
            val retriever = MediaMetadataRetriever()
            var bitmap: Bitmap? = null
            try {
                retriever.setDataSource(filePath)
                val albumArt = retriever.embeddedPicture
                if (albumArt != null) {
                    bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
                }
            } catch (e: Exception) {
                Log.e("ImageLoader", "Error loading album art", e)
            } finally {
                retriever.release()
            }

            withContext(Dispatchers.Main) {
                // Check if the ImageView is still attached to a window
                if (imageView.isAttachedToWindow) {
                    if (bitmap != null) {
                        Glide.with(imageView.context)
                            .load(bitmap)
                            .placeholder(R.drawable.baseline_play_arrow_24)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(imageView)
                    } else {
                        Glide.with(imageView.context)
                            .load(R.drawable.ic_launcher_foreground)
                            .into(imageView)
                    }
                }
            }
        }
    }
}

