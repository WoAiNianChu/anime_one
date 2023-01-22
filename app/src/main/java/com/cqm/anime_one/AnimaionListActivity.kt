package com.cqm.anime_one

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cqm.anime_one.databinding.ActivityAnimationListBinding
import com.cqm.anime_one.network.AnimeApi
import org.jsoup.Jsoup
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnimationListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnimationListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityAnimationListBinding>(this, R.layout.activity_animation_list)
        val layoutManager = LinearLayoutManager(this)
        binding.list.layoutManager = layoutManager

        sendRequestToAllList()
    }

    private fun sendRequestToAllList() {
        val timestamp = System.currentTimeMillis()

        AnimeApi.allListRetrofitService.getAllList(timestamp.toString()).enqueue(
            object: Callback<List<List<String>>> {
                override fun onResponse(
                    call: Call<List<List<String>>>,
                    response: Response<List<List<String>>>
                ) {
                    val result = response.body()
                    binding.list.adapter = result?.let { RecyclerAdapter(it) }
                }

                override fun onFailure(call: Call<List<List<String>>>, t: Throwable) {
                }
            }
        )
    }

    private class RecyclerAdapter(private val animationList: List<List<String>>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.animation_list_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
            var name = animationList[position][1]
            if (animationList[position][0] == "0") {
                name = Jsoup.parse(animationList[position][1]).text()
            }

            holder.itemName.text = name
            holder.itemEpisodeName.text = "集数：${animationList[position][2]}"
            holder.itemSeasonName.text = "年份：${animationList[position][3] + animationList[position][4]}"
        }

        override fun getItemCount(): Int {
            return animationList.size
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var itemName: TextView
            var itemEpisodeName: TextView
            var itemSeasonName: TextView

            init {
                itemName = itemView.findViewById<TextView>(R.id.name)
                itemEpisodeName = itemView.findViewById<TextView>(R.id.episodeName)
                itemSeasonName = itemView.findViewById<TextView>(R.id.seasonName)

                itemView.setOnClickListener {
                    val position = adapterPosition
                    val intent = Intent(it.context, AnimateDetailActivity::class.java)
                    if (animationList[position][0] == "0") {
                        intent.putExtra("catLink", Jsoup.parse(animationList[position][1]).select("a").attr("href"))
                    } else {
                        intent.putExtra("catLink", "/?cat=" + animationList[position][0])
                    }
                    it.context.startActivity(intent)
                }

                itemView.setOnFocusChangeListener {v, hasFocus ->
                    if (hasFocus) {
                        v.setBackgroundColor(Color.parseColor("#eeeeee"))
                    } else {
                        v.setBackgroundColor(Color.parseColor("#ffffff"))
                    }
                }
            }
        }

    }
}