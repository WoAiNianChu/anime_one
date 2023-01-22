package com.cqm.anime_one

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.cqm.anime_one.databinding.ActivityRecentlySeasonBinding

data class SeasonItem(
    val seasonName: String,
    val url: String
)

class RecentlySeasonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentlySeasonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recently_season)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recently_season)

        val seasonList = listOf(
            SeasonItem("2022年秋季新番", "https://anime1.me/2022%e5%b9%b4%e7%a7%8b%e5%ad%a3%e6%96%b0%e7%95%aa"),
            SeasonItem("2022年夏季新番", "https://anime1.me/2022%E5%B9%B4%E5%A4%8F%E5%AD%A3%E6%96%B0%E7%95%AA"),
            SeasonItem("2022年春季新番", "https://anime1.me/2022%E5%B9%B4%E6%98%A5%E5%AD%A3%E6%96%B0%E7%95%AA"),
            SeasonItem("2022年冬季新番", "https://anime1.me/2022%E5%B9%B4%E5%86%AC%E5%AD%A3%E6%96%B0%E7%95%AA"),
            SeasonItem("2021年秋季新番", "https://anime1.me/2021%e5%b9%b4%e7%a7%8b%e5%ad%a3%e6%96%b0%e7%95%aa"),
            SeasonItem("2021年夏季新番", "https://anime1.me/2021%E5%B9%B4%E5%A4%8F%E5%AD%A3%E6%96%B0%E7%95%AA"),
            SeasonItem("2021年春季新番", "https://anime1.me/2021%E5%B9%B4%E6%98%A5%E5%AD%A3%E6%96%B0%E7%95%AA"),
            SeasonItem("2021年冬季新番", "https://anime1.me/2021%E5%B9%B4%E5%86%AC%E5%AD%A3%E6%96%B0%E7%95%AA"),
        )

        for (item in seasonList) {
            val layoutInflater = LayoutInflater.from(this)
            val rowMain = layoutInflater.inflate(R.layout.season_row, null, false)
            rowMain.findViewById<TextView>(R.id.seasonName).text = item.seasonName
            rowMain.setOnClickListener {
                val detailIntent = Intent(this, SeasonListActivity::class.java)
                detailIntent.putExtra("seasonUrl", item.url)
            }
            rowMain.setOnFocusChangeListener { _v, hasFocus ->
                if (hasFocus) {
                    _v.setBackgroundColor(Color.parseColor("#eeeeee"))
                } else {
                    _v.setBackgroundColor(Color.parseColor("#ffffff"))
                }
            }
            binding.mainContainer.addView(rowMain)
        }
    }
}