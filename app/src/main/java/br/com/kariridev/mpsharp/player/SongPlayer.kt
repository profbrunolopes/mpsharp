package br.com.kariridev.mpsharp.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri

class SongPlayer(val context: Context) {

    private val mediaPlayer:MediaPlayer = MediaPlayer()
    private var isPrepared = false

    init {

        mediaPlayer.setOnPreparedListener {
            isPrepared = true
        }
    }

    fun prepare(uri: Uri){
        if(!isPrepared){
            mediaPlayer.apply {
                setAudioAttributes(AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build())
                setDataSource(context, uri)
                prepare()
            }
        }
    }

    fun play(){
        if(isPrepared && !mediaPlayer.isPlaying){
            mediaPlayer.apply {
                start()
            }
        }
    }

    fun pause(){
        if(isPrepared && mediaPlayer.isPlaying){
            mediaPlayer.apply {
                pause()
            }
        }
    }

    fun stop(){
        if(mediaPlayer.isPlaying){
            mediaPlayer.stop()
        }
    }

    fun seekTo(msec:Int){
        mediaPlayer.seekTo(msec)
    }

    fun isPlaying(): Boolean{
        return mediaPlayer.isPlaying
    }

    fun currentPosition():Int{
        return mediaPlayer.currentPosition
    }

}