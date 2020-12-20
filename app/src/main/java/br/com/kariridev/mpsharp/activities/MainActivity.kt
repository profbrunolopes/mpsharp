package br.com.kariridev.mpsharp.activities

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.kariridev.mpsharp.R
import br.com.kariridev.mpsharp.adapter.SongAdapter
import br.com.kariridev.mpsharp.adapter.SongClickListener
import br.com.kariridev.mpsharp.entity.Song


class MainActivity : AppCompatActivity(), SongClickListener{

    private lateinit var mSongsList:RecyclerView

    private val songs = mutableListOf<Song>()
    private val MY_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkReadPermission()

        val llm = LinearLayoutManager(this)
        val songAdapter = SongAdapter(songs)
        songAdapter.setListerner(this)

        mSongsList = findViewById(R.id.main_recyclerview_song_list)
        mSongsList.apply {
            layoutManager = llm
            adapter = songAdapter
        }

    }

    override fun onStart() {
        super.onStart()
        Log.i("App", "${songs.size}")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("App", "Permissão concedida")
                } else {
                    Log.i("App", "Permissão não concedida")
                }
            }
        }
    }

    private fun checkReadPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                readMusicFiles()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_REQUEST_CODE
                )
            }
        }
    }

    private fun readMusicFiles() {
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
        val selectionArgs = arrayOf("0")
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        val query = contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                if(name.endsWith(".mp3")){
                    val song = Song(id, name, duration, contentUri)
                    songs.add(song)
                }

            }
        }
    }

    override fun onSongClick(view: View, iv: ImageView, position: Int) {
        val it = Intent(this, PlayerActivity::class.java)
        it.putExtra("song", songs[position])
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, iv as View,"songimage")

        startActivity(it, options.toBundle())
    }

}