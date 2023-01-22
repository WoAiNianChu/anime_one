package com.cqm.anime_one

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.cqm.anime_one.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        this.initFocusEvent()
        this.initClickEvent()
    }

    private fun initFocusEvent() {
        listOf(binding.view, binding.view2, binding.view3).forEach {
            it.setOnFocusChangeListener { _, hasFocus ->
                setFocusHighlight(it, hasFocus)
            }
        }
    }

    private fun setFocusHighlight(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            v.setBackgroundColor(Color.argb(122, 255, 33, 33))
        } else {
            v.setBackgroundColor(Color.argb(255, 98, 0, 238))
        }
    }

    private fun initClickEvent() {
        binding.view.setOnClickListener {
            val seasonIntent = Intent(this, SeasonListActivity::class.java)
            seasonIntent.putExtra("seasonUrl", "")
            this.startActivity(seasonIntent)
        }
        binding.view2.setOnClickListener {
            val animationListIntent = Intent(this, AnimationListActivity::class.java)
            this.startActivity(animationListIntent)
        }
        binding.view3.setOnClickListener {
            val recentSeasonIntent = Intent(this, RecentlySeasonActivity::class.java)
            this.startActivity(recentSeasonIntent)
        }
    }
}