package br.com.kariridev.mpsharp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.kariridev.mpsharp.R
import br.com.kariridev.mpsharp.entity.Song
import java.text.SimpleDateFormat
import java.util.*

class SongAdapter(val songs:List<Song>):RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var listener:SongClickListener? = null

    class SongViewHolder(val itemView: View, listener: SongClickListener?): RecyclerView.ViewHolder(itemView){
        val image:ImageView = itemView.findViewById(R.id.song_image)
        val name:TextView = itemView.findViewById(R.id.item_song_name)
        val duration:TextView = itemView.findViewById(R.id.item_song_duration)

        init {
            itemView.setOnClickListener {
                listener?.onSongClick(it, image, adapterPosition)
            }
        }
    }

    fun setListerner(l:SongClickListener){
        this.listener = l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView =LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.name.text = songs[position].name
        holder.duration.text = SimpleDateFormat("m:ss", Locale.US).format(songs[position].duration)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

}