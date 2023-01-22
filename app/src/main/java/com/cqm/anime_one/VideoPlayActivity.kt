package com.cqm.anime_one

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import com.cqm.anime_one.databinding.ActivityVideoPlayBinding

class VideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        binding = DataBindingUtil.setContentView<ActivityVideoPlayBinding>(
            this,
            R.layout.activity_video_play
        )

        var videoUrl = intent.getStringExtra("videoUrl")
        if (videoUrl != "" && videoUrl != null) {
            videoUrl = if (videoUrl.indexOf("http") > 0) videoUrl else "https:${videoUrl}"
        }

        val headers = HashMap<String, String>()
        headers["User-Agent"] = "Chrome"
        headers["Cookie"] = intent.getStringExtra("cookie") ?: ""

        val uri = Uri.parse(videoUrl)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoPlayer)
        binding.videoPlayer.setMediaController(mediaController)
        mediaController.setMediaPlayer(binding.videoPlayer)
        binding.videoPlayer.setVideoURI(uri, headers)
        binding.videoPlayer.start()


//        mediaController.show(10000)
    }
}