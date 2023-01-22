package com.cqm.anime_one

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.cqm.anime_one.databinding.ActivitySeasonListBinding
import com.cqm.anime_one.network.AnimeApi
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SeasonListActivity : AppCompatActivity() {
    data class AnimeItem(
        val name: String,
        val isUpload: Boolean,
        val href: String,
    )
    private lateinit var binding: ActivitySeasonListBinding
    private var currentFocus:String = ""
    private object WeekMap {
        var Sunday = mutableListOf<AnimeItem>()
        var Monday = mutableListOf<AnimeItem>()
        var Tuesday = mutableListOf<AnimeItem>()
        var Wednesday = mutableListOf<AnimeItem>()
        var Thursday = mutableListOf<AnimeItem>()
        var Friday = mutableListOf<AnimeItem>()
        var Saturday = mutableListOf<AnimeItem>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val seasonUrl = intent.getStringExtra("seasonUrl")
        val weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        currentFocus = "textView${weekday}"

        binding = DataBindingUtil.setContentView<ActivitySeasonListBinding>(this, R.layout.activity_season_list)

        initFocusEvent()
        initClickEvent()

        when (weekday) {
            1 -> binding.textView7.requestFocus()
            2 -> binding.textView1.requestFocus()
            3 -> binding.textView2.requestFocus()
            4 -> binding.textView3.requestFocus()
            5 -> binding.textView4.requestFocus()
            6 -> binding.textView5.requestFocus()
            7 -> binding.textView6.requestFocus()
        }

        if (seasonUrl != "" && seasonUrl != null) {
            sendRequestToSeasonPage(seasonUrl)
        } else {
            sendRequestToHomePage()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.list.adapter = null
        WeekMap.Sunday.clear()
        WeekMap.Monday.clear()
        WeekMap.Tuesday.clear()
        WeekMap.Wednesday.clear()
        WeekMap.Thursday.clear()
        WeekMap.Friday.clear()
        WeekMap.Saturday.clear()
    }

    private fun initFocusEvent() {
        listOf(binding.textView1, binding.textView2, binding.textView3, binding.textView4, binding.textView5, binding.textView6, binding.textView7).forEach {
            it.setOnFocusChangeListener { view, hasFocus ->
                val focus = this.currentFocus
                val idName = view.resources.getResourceEntryName(view.id)
                setDay(idName)
                setFocusHighlight(it, hasFocus)
            }
        }
    }

    private fun setFocusHighlight(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            v.setBackgroundColor(Color.argb(122, 255, 33, 33))
        } else {
            v.setBackgroundColor(Color.argb(255, 187, 134, 252))
        }
    }

    private fun getWeekMapValueFromId(idName: String):MutableList<AnimeItem> {
        var result = WeekMap.Sunday
        when(idName) {
            "textView1" -> {
                result = WeekMap.Monday
            }
            "textView2" -> {
                result = WeekMap.Tuesday
            }
            "textView3" -> {
                result = WeekMap.Wednesday
            }
            "textView4" -> {
                result = WeekMap.Thursday
            }
            "textView5" -> {
                result = WeekMap.Friday
            }
            "textView6" -> {
                result = WeekMap.Saturday
            }
            "textView7" -> {
                result = WeekMap.Sunday
            }
        }
        return result
    }

    private fun setDay(idName: String) {
        currentFocus = idName
        binding.list.adapter = ListAdapter(this, getWeekMapValueFromId(idName))
    }

    private fun initClickEvent() {
        binding.list.setOnItemClickListener { _, _, position, _ ->
            val animateDayList = getWeekMapValueFromId(currentFocus)
            if (animateDayList[position].href != "") {
                val detailIntent = Intent(this, AnimateDetailActivity::class.java)
                detailIntent.putExtra("catLink", animateDayList[position].href)
                this.startActivity(detailIntent)
            } else {
                Toast.makeText(this, "该番剧暂无资源", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendRequestToHomePage() {
        AnimeApi.webUrlRetrofitService.getHomePage().enqueue(
            object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val html = response.body()
                    val doc = Jsoup.parse(html)

                    val link = doc.select("#primary-menu li:nth-child(2) a").attr("href")
                    sendRequestToSeasonPage(link)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.i("SeasonListFragment失败", t.message.toString())
                }
            }
        )
    }

    private fun sendRequestToSeasonPage(link: String) {
        AnimeApi.webUrlRetrofitService.getSeasonPage(link).enqueue(
            object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val html = response.body()
                    val doc = Jsoup.parse(html)

                    val size = doc.select("tbody tr").size
                    for (i in 1 until size) {
                        val tds = doc.select("tbody tr:nth-child(${i}) td")
                        for (j in 1..tds.size) {
                            val td = doc.select("tbody tr:nth-child(${i}) td:nth-child(${j})")

                            if (td.text() !== "") {
                                setAnimationMap(
                                    td.text(),
                                    !td.select("a").isEmpty(),
                                    td.select("a").attr("href"),
                                    j
                                )
                            }
                        }
                    }
                    setDay(currentFocus)
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.i("SeasonListFragment失败", t.message.toString())
                }
            })
    }

    private class ListAdapter(
        context: Context,
        items: List<AnimeItem>
    ): BaseAdapter() {
        private val items:List<AnimeItem>
        private val mContext: Context

        init {
            this.items = items
            this.mContext = context
        }
        override fun getCount(): Int {
            return this.items.size;
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getItem(position: Int): AnimeItem {
            return this.items[position]
        }
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.animation_row, viewGroup, false)
            rowMain.findViewById<TextView>(R.id.name).text = items[position].name
            rowMain.findViewById<TextView>(R.id.upload).text = if(items[position].isUpload) "已上传" else "未上传"
            if (items[position].isUpload) {
                rowMain.findViewById<TextView>(R.id.upload).text = "已上传"
                rowMain.findViewById<TextView>(R.id.upload).setTextColor(Color.parseColor("#3CB371"))
            } else {
                rowMain.findViewById<TextView>(R.id.upload).text = "未上传"
                rowMain.findViewById<TextView>(R.id.upload).setTextColor(Color.parseColor("#FF6347"))
            }

            return rowMain
        }
    }

    private fun setAnimationMap(animateName: String, isUpload: Boolean, link: String, index: Number) {

        when(index) {
            1 -> {
                // 周日
                val item = AnimeItem(animateName, isUpload, link)
                WeekMap.Sunday.add(item)
            }
            2 -> {
                // 周一
                val item = AnimeItem(animateName, isUpload, link)
                WeekMap.Monday.add(item)
            }
            3 -> {
                // 周二
                val item = AnimeItem(animateName, isUpload, link)
                WeekMap.Tuesday.add(item)
            }
            4 -> {
                // 周三
                val item = AnimeItem(animateName, isUpload, link)
                WeekMap.Wednesday.add(item)
            }
            5 -> {
                // 周四
                val item = AnimeItem(animateName, isUpload, link)
                WeekMap.Thursday.add(item)
            }
            6 -> {
                // 周五
                val item = AnimeItem(animateName, isUpload, link)
                WeekMap.Friday.add(item)
            }
            7 -> {
                // 周六
                val item = AnimeItem(animateName, isUpload, link)
                WeekMap.Saturday.add(item)
            }
        }
    }
}