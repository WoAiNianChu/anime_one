package com.cqm.anime_one

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.cqm.anime_one.databinding.ActivityAnimateDetailBinding
import com.cqm.anime_one.network.AnimationType
import com.cqm.anime_one.network.AnimeApi
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder

data class EpisodeItem (
    val name:String,
    val apiReq:String,
    val dateStr: String,
    val videoUrl: String
)

class AnimateDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnimateDetailBinding
    private var episodeList = mutableListOf<EpisodeItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val catLink = intent.getStringExtra("catLink")
        binding = DataBindingUtil.setContentView<ActivityAnimateDetailBinding>(this, R.layout.activity_animate_detail)
        if (catLink != null) {
            getAnimateDetail(catLink)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        episodeList.clear()
        binding.container.removeAllViews()
    }

    private fun getAnimateDetail(catLink:String) {
        val link = if (catLink.startsWith("http")) catLink else "https://anime1.me${catLink}"
        val activity = this
        AnimeApi.webUrlRetrofitService.getDetailPage(link).enqueue(
            object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val html = response.body()
                    val doc = Jsoup.parse(html?:"")

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
            goToVideoPage("", videoUrl)
        }
    }

    private fun goToVideoPage(cookie: String, videoUrl: String) {
        val videoIntent = Intent(this, VideoPlayActivity::class.java)
        videoIntent.putExtra("cookie", cookie)
        videoIntent.putExtra("videoUrl", videoUrl)
        this.startActivity(videoIntent)
    }
}