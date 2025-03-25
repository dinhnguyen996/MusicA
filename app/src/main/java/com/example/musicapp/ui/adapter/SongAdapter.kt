package com.example.musicapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.ItemSongBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.utils.GlobalFunctions

class SongAdapter(private val onBookClick: IOnItemClick) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private var dataSet: List<Song> = listOf()
    private var lastPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemSongBinding: ItemSongBinding = ItemSongBinding.bind(view)
        val title = itemSongBinding.tvSongName
        val artist = itemSongBinding.tvArtist
        val image = itemSongBinding.imgSong
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_song, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.title.text = dataSet[position].title
        GlobalFunctions.loadAlbumArt(
            viewHolder.image.context, viewHolder.image, dataSet[position].albumId
        )
        viewHolder.artist.text = dataSet[position].artist
        viewHolder.itemView.setOnClickListener {
            onBookClick.onItemClick(dataSet[position], false, it)
        }
        viewHolder.itemView.setOnLongClickListener { view ->
            onBookClick.onItemClick(dataSet[position], true, view)
            true
        }

        // Apply the desired animations based on the scroll direction
        if (position > lastPosition) {
            // Scroll up, apply translation up and fade in animation
            viewHolder.itemView.alpha = 0f
            viewHolder.itemView.translationY = 100f
            viewHolder.itemView.animate().alpha(1f).translationY(0f).setDuration(500).start()
        } else {
            // Scroll down, apply translation down and fade in animation
            viewHolder.itemView.alpha = 0f
            viewHolder.itemView.translationY = -100f
            viewHolder.itemView.animate().alpha(1f).translationY(0f).setDuration(500).start()
        }
    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newData: List<Song>) {
        this.dataSet = newData
        notifyDataSetChanged()
    }
}