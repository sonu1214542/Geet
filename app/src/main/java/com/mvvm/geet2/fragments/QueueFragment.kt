package com.mvvm.geet2.fragments

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.geet2.MainActivity
import com.mvvm.geet2.R
import com.mvvm.geet2.viewadapters.AllSongsAdapter
import com.mvvm.geet2.viewadapters.QueueAdapter
import com.mvvm.geet2.viewmodels.PlayerViewmodel
import com.mvvm.geet2.viewmodels.QueueViewModel
import java.util.Collections

class QueueFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var queueAdapter: QueueAdapter
    private val queueViewModel: QueueViewModel by viewModels()
    private val playerViewmodel : PlayerViewmodel by viewModels({requireActivity()})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_queue, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.queueRecyclerView)
        val noQueueText = view.findViewById<TextView>(R.id.noQueueText)
        recyclerView.layoutManager=LinearLayoutManager(context)
        queueAdapter= QueueAdapter(queueViewModel.queueList)
        recyclerView.adapter=queueAdapter

        if (queueViewModel.queueList.value!!.isEmpty()){
            noQueueText.visibility=View.VISIBLE
        }else{
            noQueueText.visibility=View.GONE
        }

        queueViewModel.queueList.observe(viewLifecycleOwner, Observer { songs ->
            if (songs.isEmpty()) {
                noQueueText.visibility = View.VISIBLE
            } else {
                noQueueText.visibility = View.GONE
            }
            queueAdapter.notifyDataSetChanged() // Use submitList for better diffing and performance
        })

        val simpleCallback :ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,0){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition

                // Get the current list
                val currentQueue = MainActivity.clickedSongQueue.value ?: mutableListOf()

                // Swap the items
                Collections.swap(currentQueue, fromPosition, toPosition)
                playerViewmodel.moveMediaItem(fromPosition, toPosition)

//                MainActivity.clickedSongQueue.value = currentQueue

                // Notify the adapter
                recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}