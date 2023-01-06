package com.cqm.anime_one.pages
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.cqm.anime_one.R
import com.cqm.anime_one.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false)

        binding.view.setOnClickListener {
            Toast.makeText(activity,"Text!",Toast.LENGTH_SHORT).show();
        }

//        activity?.window?.let {
//            WindowCompat.setDecorFitsSystemWindows(it, false)
//            WindowInsetsControllerCompat(it, binding.root).let { controller ->
//                controller.hide(WindowInsetsCompat.Type.systemBars())
//                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            }
//        }
        this.initFocusEvent()
        this.initClickEvent()

        return binding.root
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
        binding.view.setOnClickListener {view ->
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSeasonListFragment(""))
        }
        binding.view2.setOnClickListener {view ->
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAnimationListFragment())
        }
        binding.view3.setOnClickListener {view ->
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRecentlyUpdateFragment())
        }
    }

}