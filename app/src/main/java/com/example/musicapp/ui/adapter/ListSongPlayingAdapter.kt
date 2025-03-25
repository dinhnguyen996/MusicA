package com.example.musicapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.ItemSongPlayingBinding
import com.example.musicapp.listener.IOnItemClick
import com.example.musicapp.utils.GlobalFunctions

class ListSongPlayingAdapter(
    private var listSong: List<Song>, private val songPlayingClickListener: IOnItemClick
) : RecyclerView.Adapter<ListSongPlayingAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_song_playing, parent, false)
        return SongViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentItem = listSong[position]
        if (currentItem.isPlaying) {
            holder.isPlaying.visibility = View.VISIBLE
        } else {
            holder.isPlaying.visibility = View.GONE
        }
        holder.title.text = currentItem.title
        holder.artist.text = currentItem.artist
        GlobalFunctions.loadAlbumArt(
            holder.itemView.context, holder.image, currentItem.albumId
        )
        holder.itemView.setOnClickListener {
            songPlayingClickListener.onItemClick(
                currentItem, false, it
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSongList(newSongList: ArrayList<Song>) {
        listSong = newSongList
        notifyDataSetChanged()
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemSongPlayingBinding: ItemSongPlayingBinding =
            ItemSongPlayingBinding.bind(itemView)

        val title: TextView = itemSongPlayingBinding.tvSongName
        val artist: TextView = itemSongPlayingBinding.tvArtist
        val image: ImageView = itemSongPlayingBinding.imgSong
        val isPlaying: ImageView = itemSongPlayingBinding.imgPlaying
    }
}