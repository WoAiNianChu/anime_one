package com.cqm.anime_one.pages

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.cqm.anime_one.R
import com.cqm.anime_one.databinding.FragmentAnimateDetailBinding
import com.cqm.anime_one.network.AnimationType
import com.cqm.anime_one.network.AnimeApi
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder

data class EpisodeItem(
    val name: String,
    val apiReq: String,
    val dateStr: String,
    val videoUrl: String,
)

class AnimateDetailFragment : Fragment() {
    private lateinit var binding: FragmentAnimateDetailBinding
    private var episodeList = mutableListOf<EpisodeItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = AnimateDetailFragmentArgs.fromBundle(requireArguments())

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_animate_detail, container, false)

        getAnimateDetail(args.catLink)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        episodeList.clear()
        binding.container.removeAllViews()
    }

    private fun getAnimateDetail(catLink:String) {
        val link = if (catLink.startsWith("http")) catLink else "https://anime1.me${catLink}"
        AnimeApi.webUrlRetrofitService.getDetailPage(link).enqueue(
            object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val html = response.body()
                    val doc = Jsoup.parse(html)

                    doc.select("article").forEach{ v ->
                        val name = v.select(".entry-title").text()
                        val apiReq = v.select("video").attr("data-apireq")
                        val dateStr = v.select("time").text()
                        var videoUrl = ""
                        if (apiReq == "") {
                            videoUrl = v.select("source").attr("src")
                        }
                        episodeList.add(EpisodeItem(name, URLDecoder.decode(apiReq, "UTF-8"), dateStr, videoUrl))
                    }

                    for (item in episodeList) {
                        val layoutInflater = LayoutInflater.from(activity)
                        val rowMain = layoutInflater.inflate(R.layout.episode_row, null, false)
                        rowMain.findViewById<TextView>(R.id.episodeName).text = item.name
                        rowMain.findViewById<TextView>(R.id.updateTime).text = item.dateStr

                        rowMain.setOnClickListener {
                            Log.i("66666661", item.videoUrl)
                            getResource(item.apiReq, item.videoUrl)
                        }

                        rowMain.setOnFocusChangeListener { _v, hasFocus ->
                            if (hasFocus) {
                                _v.setBackgroundColor(Color.parseColor("#eeeeee"))
                            } else {
                                _v.setBackgroundColor(Color.parseColor("#ffffff"))
                            }
                        }

                        binding.container.addView(rowMain)
                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            }
        )
    }

    private fun getResource(apiReq: String, videoUrl: String) {
        if (apiReq != "") {
            Log.i("6666666662", apiReq)
            val params = HashMap<String?, String?>()
            params["d"] = apiReq
            AnimeApi.apiUrlRetrofitService.getResource(params).enqueue(
                object : Callback<AnimationType> {
                    override fun onResponse(
                        call: Call<AnimationType>,
                        response: Response<AnimationType>
                    ) {
                        val result = response.body()
                        val headers = response.headers()
                        val headerList = headers.values("set-cookie")

                        result?.s?.get(0)?.let { Log.i("666666666666", it.src) }

                        var cookieStr = ""
                        for (cookieItem in headerList) {
                            cookieStr += (cookieItem.split(";")[0] + ";")
                        }
                        result?.s?.get(0)?.src?.let { goToVideoPage(cookieStr, it) }
                    }

                    override fun onFailure(call: Call<AnimationType>, t: Throwable) {
                        Log.i("666666失败", t.message.toString())
                    }
                }
            )
        } else if(videoUrl != "") {
            Log.i("6666666663", videoUrl)
            goToVideoPage("", videoUrl)
        }
    }
    
    private fun goToVideoPage(cookie: String, videoUrl: String) {
        findNavController().navigate(AnimateDetailFragmentDirections.actionAnimateDetailFragmentToVideoPlayFragment(
            cookie, videoUrl
        ))
    }
}