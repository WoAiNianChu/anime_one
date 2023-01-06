package com.cqm.anime_one.pages

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cqm.anime_one.R
import com.cqm.anime_one.databinding.FragmentRecentlySeasonBinding

data class SeasonItem(
    val seasonName: String,
    val url: String
)

class RecentlySeasonFragment : Fragment() {

    private lateinit var binding: FragmentRecentlySeasonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_recently_season, container, false)

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
            val layoutInflater = LayoutInflater.from(activity)
            val rowMain = layoutInflater.inflate(R.layout.season_row, null, false)
            rowMain.findViewById<TextView>(R.id.seasonName).text = item.seasonName
            rowMain.setOnClickListener {
                findNavController().navigate(RecentlySeasonFragmentDirections.actionRecentlySeasonFragmentToSeasonListFragment(item.url))
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

        return binding.root
    }
}