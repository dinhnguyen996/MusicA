package com.example.musicapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.data.model.Playlist
import com.example.musicapp.databinding.ItemPlaylistBinding
import com.example.musicapp.listener.IOnItemClick

class PlaylistAdapter(
    private val listener: IOnItemClick
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private var playlist: List<Playlist> = emptyList()

    class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: ItemPlaylistBinding = ItemPlaylistBinding.bind(view)
        val playlistName: TextView = binding.tvPlaylistName
        val timeCreated: TextView = binding.tvTimeCreated
        val playlistImage: ImageView = binding.imgPlaylist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.playlistName.text = playlist[position].name
        holder.timeCreated.text = "Created in ${playlist[position].timeCreated}"
        Glide.with(holder.playlistImage.context).load(R.drawable.music_note_24px)
            .error(R.drawable.music_note_24px).into(holder.playlistImage)
        holder.itemView.setOnClickListener {
            listener.onItemClick(playlist[position], false, it)
        }
        holder.itemView.setOnLongClickListener {
            listener.onItemClick(playlist[position], true, it)
            true
        }
    }

    override fun getItemCount(): Int = playlist.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(playlists: List<Playlist>) {
        this.playlist = playlists
        notifyDataSetChanged()
    }
}