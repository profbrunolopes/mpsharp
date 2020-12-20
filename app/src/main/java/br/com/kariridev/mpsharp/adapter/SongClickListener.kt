package br.com.kariridev.mpsharp.adapter

import android.view.View
import android.widget.ImageView

interface SongClickListener {

    fun onSongClick(view: View, iv: ImageView, position:Int)

}