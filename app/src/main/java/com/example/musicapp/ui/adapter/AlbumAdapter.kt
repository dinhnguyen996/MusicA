package com.example.musicapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.data.model.Album
import com.example.musicapp.databinding.ItemAlbumBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.utils.GlobalFunctions

class AlbumAdapter(private val onBookClick: IOnItemClick) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    private var dataSet: List<Album> = listOf()
    private var lastPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemAlbumBinding: ItemAlbumBinding = ItemAlbumBinding.bind(view)
        val title = itemAlbumBinding.tvAlbumName
        val trackCount = itemAlbumBinding.tvTrackCount
        val image = itemAlbumBinding.imgAlbum
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_album, viewGroup, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.title.text = dataSet[position].name
        GlobalFunctions.loadAlbumArt(
            viewHolder.image.context, viewHolder.image, dataSet[position].id
        )
        val songCount = dataSet[position].songCount.toString()
        viewHolder.trackCount.text = "$songCount song${if (songCount == "1") "" else "s"}"
        viewHolder.itemView.setOnClickListener {
            onBookClick.onItemClick(dataSet[position], false, it)
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
    fun submitList(newData: List<Album>) {
        this.dataSet = newData
        notifyDataSetChanged()
    }
}