package com.mvvm.geet2

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mvvm.geet2.AllSongsRepo.Companion.songsLiveData
import com.mvvm.geet2.entities.Song
import com.mvvm.geet2.fragments.AlbumFragment
import com.mvvm.geet2.fragments.AlbumFragmentHolder
import com.mvvm.geet2.fragments.AllSongsFragment
import com.mvvm.geet2.fragments.PlaylistFragment
import com.mvvm.geet2.fragments.QueueFragment
import com.mvvm.geet2.viewmodels.AllSongsViewModel
import com.mvvm.geet2.viewmodels.PlayerViewmodel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private lateinit var allSongsViewModel: AllSongsViewModel
//    lateinit var miniplayer : View
    lateinit var playerViewmodel: PlayerViewmodel
    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout
    companion object{
        var _clickedPos = MutableStateFlow(0)
        val clickedPos = _clickedPos.asStateFlow()
        var clickedSongQueue: MutableLiveData<MutableList<Songs>> = MutableLiveData(mutableListOf())
        suspend fun newQueue(songs: MutableList<Songs>) {
            clickedSongQueue.value?.clear()
            clickedSongQueue.postValue(songs)
        }
        var songToAddToPlaylist:Song?=null
//        fun getQueueFlow():Flow<List<Songs>> = clickedSongQueue.asFlow()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==9){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT).show()
                initviews()
            }else{
                requestRuntimePermission()
            }
        }
    }

    private fun requestRuntimePermission(){
        if (Build.VERSION.SDK_INT>=33){
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),9)
                Toast.makeText(this,"permission not granted,go to app settings and allow requested permissions to run the app",Toast.LENGTH_SHORT).show()
            }else{
                initviews()
            }
        }else{
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),9)
                Toast.makeText(this,"permission not granted,go to app settings and allow requested permissions to run the app",Toast.LENGTH_SHORT).show()
            }else{
                initviews()
            }
        }
    }

    private fun initviews(){
        allSongsViewModel=ViewModelProvider(this).get(AllSongsViewModel::class.java)
        playerViewmodel = ViewModelProvider(this).get(PlayerViewmodel::class.java)
        allSongsViewModel.registerContentObserver()
        val miniplayer = findViewById<View>(R.id.miniplayer)
        val miniplayerTitle : TextView = miniplayer.findViewById(R.id.currentSong)
        val playButton : ImageButton = miniplayer.findViewById(R.id.playButton)
        val playNextButton : ImageButton = miniplayer.findViewById(R.id.nextButton)
        val prevButton : ImageButton = miniplayer.findViewById(R.id.prevButton)
        val artwork : ImageView = miniplayer.findViewById(R.id.imageView)
        val seekBar : SeekBar = miniplayer.findViewById(R.id.currentProgressBar)

        viewPager = findViewById(R.id.viewpager)
        tabLayout= findViewById(R.id.tablayout)

        val myPagerAdapter = MyPagerAdapter(this)
        viewPager.adapter=myPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = myPagerAdapter.getPageTitle(position)
        }.attach()

        playerViewmodel.initializeMediaController(this)
        playerViewmodel.mediaMetadataLiveData.observe(this, Observer { medimetadata->
            Log.d("MainActivity", "Media Metadata Title: ${medimetadata.title}")
            miniplayerTitle.text=medimetadata.title
            Glide.with(this).load(medimetadata.artworkData).into(artwork)
        })

        playButton.setOnClickListener {
            if (playerViewmodel.isPlayingLiveData.value == true) {
                playerViewmodel.Pause()
            } else {
                playerViewmodel.Play()
            }
        }

        prevButton.setOnClickListener{
            playerViewmodel.PlayPrev()
        }

        playNextButton.setOnClickListener{
            playerViewmodel.PlayNext()
        }
        playerViewmodel.isPlayingLiveData.observe(this,Observer{isplaying->
            if (isplaying) {
                playButton.setImageResource(R.drawable.baseline_pause_24)
            } else {
                playButton.setImageResource(R.drawable.baseline_play_arrow_24)
            }

        })
        playerViewmodel.durationLiveData.observe(this, Observer { duration ->
            seekBar.max = (duration / 1000).toInt()  // Convert to seconds if the SeekBar's max is in seconds
        })

        // Observing position to update the SeekBar
        playerViewmodel.positionLiveData.observe(this, Observer { pos ->
            Log.e("progres",pos.toString())
            seekBar.progress = (pos / 1000).toInt()  // Convert to seconds if the SeekBar's max is in seconds
        })

// Set up a listener for user interactions with the SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playerViewmodel.SeekTo(progress*1000L)
//                    playerViewmodel.mediaController.seekTo(progress * 1000L) // Convert to milliseconds
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Optional: Add any specific action when the user starts touching the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Optional: Add any specific action when the user stops touching the SeekBar
            }
        })
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        requestRuntimePermission()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        if (this::allSongsViewModel.isInitialized){
            allSongsViewModel.unregisterContentObserver()
        }
    }
    class MyPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        private val fragmentNames = arrayOf("Songs", "Albums","Playlists","Queue")

        override fun getItemCount(): Int = fragmentNames.size

        @OptIn(UnstableApi::class)
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AllSongsFragment()
                1 -> AlbumFragmentHolder()
                2 -> PlaylistFragment()
                3 -> QueueFragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }

        fun getPageTitle(position: Int): CharSequence? {
            return fragmentNames[position]
        }
    }
}