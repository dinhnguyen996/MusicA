package com.example.musicapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.databinding.ItemArtistBinding
import com.example.musicapp.listener.IOnItemClick

class ArtistAdapter(private val onItemClick: IOnItemClick) :
    RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    private var dataSet: List<String> = listOf()
    private var lastPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemArtistBinding: ItemArtistBinding = ItemArtistBinding.bind(view)
        val name = itemArtistBinding.tvArtistName
        val image = itemArtistBinding.imgArtist
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_artist, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = dataSet[position]
        Glide.with(viewHolder.image.context).load(R.drawable.image_no_available)
            .into(viewHolder.image)
        viewHolder.image.setOnClickListener {
            onItemClick.onItemClick(dataSet[position], false, it)
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
    fun submitList(newData: List<String>) {
        this.dataSet = newData
        notifyDataSetChanged()
    }
}