package com.cqm.anime_one.pages

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.cqm.anime_one.R
import com.cqm.anime_one.databinding.FragmentVideoPlayBinding

class VideoPlayFragment : Fragment() {
    private lateinit var binding: FragmentVideoPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_video_play, container, false)

        val args = VideoPlayFragmentArgs.fromBundle(requireArguments())

        val cookie = args.cookie
        val videoUrl = if (args.videoUrl.indexOf("http") > 0) args.videoUrl else "https:${args.videoUrl}"

        val headers = HashMap<String, String>()
        headers["User-Agent"] = "Chrome"
        headers["Cookie"] = cookie
        val uri = Uri.parse(videoUrl)

        val mediaController = MediaController(activity)
        binding.videoPlayer.setMediaController(mediaController)
        mediaController.setMediaPlayer(binding.videoPlayer)
        binding.videoPlayer.setVideoURI(uri, headers)
        binding.videoPlayer.start()
        return binding.root
    }

    fun onMyKeyDown(keyCode: Int) {
        val currentPosition = binding.videoPlayer.currentPosition
//        if (keyCode == 21) {
//            binding.videoPlayer.seekTo(100000)
//        }
//        if (keyCode == 22) {
//            binding.videoPlayer.seekTo(800000)
//        }

    }

//    private fun hideNavbar() {
//        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
//        activity?.window?.let {
//            WindowInsetsControllerCompat(it, binding.mainContainer).let { controller ->
//                controller.hide(WindowInsetsCompat.Type.systemBars())
//                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            }
//        }
//    }
//
//    private fun showNavbar() {
//        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
//        activity?.window?.let {
//            WindowInsetsControllerCompat(it, binding.mainContainer).show(WindowInsetsCompat.Type.systemBars())
//        }
//    }

}