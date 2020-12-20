package br.com.kariridev.mpsharp.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import br.com.kariridev.mpsharp.R
import br.com.kariridev.mpsharp.entity.Song
import br.com.kariridev.mpsharp.player.SongPlayer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class PlayerActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener,
    SensorEventListener {

    private lateinit var song: Song
    private lateinit var songPlayer: SongPlayer
    private lateinit var updateUI: Timer

    private lateinit var mActionButton: ImageView
    private lateinit var mNextButton: ImageView
    private lateinit var mPreviousButton: ImageView
    private lateinit var mSeekBar: SeekBar
    private lateinit var mActualMsec: TextView
    private lateinit var mDuration: TextView
    private lateinit var mSongName: TextView

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    private val handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        song = intent.getParcelableExtra<Song>("song")!!
        songPlayer = SongPlayer(this)
        songPlayer.prepare(song.uri)

        mActionButton = findViewById(R.id.player_imageview_song_action)
        mActionButton.setOnClickListener(this)

        mNextButton = findViewById(R.id.player_imageview_song_forward)
        mNextButton.setOnClickListener(this)

        mPreviousButton = findViewById(R.id.player_imageview_song_backward)
        mPreviousButton.setOnClickListener(this)

        mSeekBar = findViewById(R.id.player_seekbar_song_duration)
        mSeekBar.setOnSeekBarChangeListener(this)
        mSeekBar.max = song.duration.toInt()

        mActualMsec = findViewById(R.id.player_textview_song_actual_msec)

        mDuration = findViewById(R.id.player_textview_song_duration)
        mDuration.text = SimpleDateFormat("m:ss", Locale.US).format(song.duration)


        mSongName = findViewById(R.id.player_textview_song_name)
        mSongName.text = song.name

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        updateUI = Timer("updateTimer")
        updateUI.schedule(Date(), 1000) {
            if (songPlayer.currentPosition() <= song.duration) {
                handler.post {
                    mSeekBar.progress = songPlayer.currentPosition()
                    mActualMsec.text =
                        SimpleDateFormat("m:ss", Locale.US).format(songPlayer.currentPosition())
                }
            } else {
                updateUI.cancel()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.also { proximity ->
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
        }

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (songPlayer.isPlaying()) {
            songPlayer.stop()
        }

        supportFinishAfterTransition()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.player_imageview_song_action -> {
                if (songPlayer.isPlaying()) {
                    songPlayer.pause()
                    mActionButton.setImageResource(R.drawable.play)
                } else {
                    songPlayer.play()
                    mActionButton.setImageResource(R.drawable.pause)
                }

            }

            R.id.player_imageview_song_forward -> {
                updateProgress(500)
            }

            R.id.player_imageview_song_backward -> {
                updateProgress(-500)
            }

        }

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            songPlayer.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val distance = event?.values?.get(0)
        if (distance == 0.0f) {
            if (songPlayer.isPlaying()) {
                songPlayer.pause()
            } else {
                songPlayer.play()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun updateProgress(msec: Int) {
        mSeekBar.progress += msec
        songPlayer.seekTo(mSeekBar.progress)
    }


}