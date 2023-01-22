package com.cqm.anime_one.network
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val WEB_URL = "https://anime1.me"
private const val API_URL = "https://v.anime1.me"
private const val ALL_LIST_URL = "https://d1zquzjgwo9yb.cloudfront.net"

data class VideoItemType (
    val src: String,
    val type: String
)

data class AnimationType (
    val s: List<VideoItemType>
)

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val webUrlRetrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(WEB_URL)
    .build()

private val apiUrlRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(API_URL)
    .build()

private val allListUrlRetrofit = Retrofit.Builder()
//    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(ALL_LIST_URL)
    .build()

interface WebUrlService {
    @GET("/")
    fun getHomePage():
            Call<String>
    @GET
    fun getSeasonPage(@Url url: String):
            Call<String>
    @GET
    fun getDetailPage(@Url url: String):
            Call<String>
}

interface ApiUrlService {
    @FormUrlEncoded
    @POST("/api")
    fun getResource(@FieldMap params: HashMap<String?, String?>):
            Call<AnimationType>
}

interface AllListService {
    @GET("/")
    fun getAllList(@Query("_") str:String):
            Call<List<List<String>>>
}

object AnimeApi {
    val webUrlRetrofitService : WebUrlService by lazy {
        webUrlRetrofit.create(WebUrlService::class.java) }
    val apiUrlRetrofitService : ApiUrlService by lazy {
        apiUrlRetrofit.create(ApiUrlService::class.java)
    }
    val allListRetrofitService: AllListService by lazy {
        allListUrlRetrofit.create(AllListService::class.java)
    }
}