package com.cqm.anime_one.pages

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.cqm.anime_one.R
import com.cqm.anime_one.databinding.FragmentSeasonListBinding
import com.cqm.anime_one.network.AnimeApi
import org.jsoup.Jsoup
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.util.*

data class AnimeItem(
    val name: String,
    val isUpload: Boolean,
    val href: String,
)

class SeasonListFragment : Fragment() {
    private lateinit var binding: FragmentSeasonListBinding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = SeasonListFragmentArgs.fromBundle(requireArguments())
        val weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        currentFocus = "textView${weekday}"

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_season_list, container, false)

        when (weekday) {
            1 -> binding.textView7.requestFocus()
            2 -> binding.textView1.requestFocus()
            3 -> binding.textView2.requestFocus()
            4 -> binding.textView3.requestFocus()
            5 -> binding.textView4.requestFocus()
            6 -> binding.textView5.requestFocus()
            7 -> binding.textView6.requestFocus()
        }

        initFocusEvent()
        initClickEvent()
        if (args.seasonUrl != "") {
            sendRequestToSeasonPage(args.seasonUrl)
        } else {
            sendRequestToHomePage()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
                val focus = activity?.currentFocus
                if (focus !== null) {
                    val idName = view.resources.getResourceEntryName(view.id)
                    setDay(idName)
                }
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
        binding.list.adapter = context?.let { ListAdapter(it, getWeekMapValueFromId(idName))}
    }

    private fun initClickEvent() {
        binding.list.setOnItemClickListener { _, _view, position, id ->
            val animateDayList = getWeekMapValueFromId(currentFocus)
            if (animateDayList[position].href != "") {
                _view.findNavController().navigate(
                    SeasonListFragmentDirections.actionSeasonListFragmentToAnimateDetailFragment(
                        animateDayList[position].href
                    )
                )
            } else {
                Toast.makeText(activity, "该番剧暂无资源", Toast.LENGTH_SHORT).show()
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
        private val mContext:Context

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
        override fun getItem(position: Int): Any {

            return "TEST STRING"
        }
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.animation_row, viewGroup, false)
            rowMain.findViewById<TextView>(R.id.name).text = items[position].name
            rowMain.findViewById<TextView>(R.id.upload).text = if(items[position].isUpload) "已上传" else "未上传"

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