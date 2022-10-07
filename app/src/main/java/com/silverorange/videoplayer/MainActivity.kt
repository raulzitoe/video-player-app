package com.silverorange.videoplayer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.silverorange.videoplayer.databinding.ActivityMainBinding
import io.noties.markwon.Markwon


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        player = ExoPlayer.Builder(this).build()
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                mediaItem?.mediaId?.let { setVideoDetails(it) }
            }
        })
        binding.videoPlayer.player = player

        viewModel.videoList.observe(this) { videoList ->
            for ((position, videoItem) in videoList.withIndex()) {
                player.addMediaItem(
                    MediaItem.Builder()
                        .setUri(videoItem.hlsURL)
                        .setMediaId(position.toString())
                        .build()
                )
            }
            player.prepare()
            setVideoDetails("0")
        }

        viewModel.error.observe(this) { isError ->
            if (isError) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.not_connected))
                    .setMessage(getString(R.string.not_connected_full))
                    .setPositiveButton(getString(R.string.ok)) { _, _ -> }
                    .show()
            }
        }
    }

    fun setVideoDetails(position: String) {
        if (viewModel.videoList.value.isNullOrEmpty()) return

        val videoItem = viewModel.videoList.value!![position.toInt()]
        val markwon = Markwon.builder(this)
            .build()

        with(binding) {
            textviewTitle.text = videoItem.title
            textviewAuthor.text = videoItem.author.name
            markwon.setMarkdown(textviewDescription, videoItem.description)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}