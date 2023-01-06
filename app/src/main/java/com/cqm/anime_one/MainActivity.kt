package com.cqm.anime_one

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.cqm.anime_one.databinding.ActivityMainBinding
import com.cqm.anime_one.pages.VideoPlayFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val navHost = supportFragmentManager.findFragmentById(R.id.myNavHostFragment)
        if (navHost != null) {
            val fragment = navHost.childFragmentManager.primaryNavigationFragment
            if (fragment is VideoPlayFragment) {
                Log.i("111111111", keyCode.toString())
                fragment.onMyKeyDown(keyCode)
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}