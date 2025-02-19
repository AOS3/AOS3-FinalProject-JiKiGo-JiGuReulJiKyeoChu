package com.protect.jikigo.ui.home.news

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.protect.jikigo.databinding.FragmentNewsAllBinding
import android.util.Log
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.protect.jikigo.data.RetrofitClient
import com.protect.jikigo.data.NewsResponse
import com.protect.jikigo.R
import com.protect.jikigo.ui.adapter.NewsAllBannerAdapter
import com.protect.jikigo.utils.cleanHtml
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class NewsAllFragment : Fragment() {
    private var _binding: FragmentNewsAllBinding? = null
    private val binding get() = _binding!!

    private val bannerImages = listOf(
        R.drawable.img_news_all_banner_1,
        R.drawable.img_news_all_banner_2,
        R.drawable.img_news_all_banner_3
    )

    // 배너 자동 넘기기에 필요한 요소들
    private val handler = Handler(Looper.getMainLooper())
    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            binding?.let {
                val nextItem = (it.vpNewsAllBanner.currentItem + 1) % bannerImages.size
                Log.d("adapter", "currentItem Index : ${it.vpNewsAllBanner.currentItem}")
                it.vpNewsAllBanner.setCurrentItem(nextItem, true)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(autoSlideRunnable)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뉴스 검색 실행
        fetchNews("환경오염")
        // 띠 배너
        setupHomeBannerUI()

    }

    // 웹사이트의 Open Graph 태그에서 이미지 URL 가져오기
    private fun fetchNewsImage(url: String, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val doc = Jsoup.connect(url).get()
                val imageUrl = doc.select("meta[property=og:image]").attr("content")
                val finalImageUrl = if (imageUrl.startsWith("http://")) {
                    // HTTP 프로토콜이 있을 경우 HTTPS로 변경
                    imageUrl.replace("http://", "https://")
                } else {
                    imageUrl
                }

                // UI 스레드에서 실행하도록 변경
                binding.root.post {
                    callback(finalImageUrl.ifEmpty { null })
                }
            } catch (e: Exception) {
                binding.root.post {
                    callback(null)
                }
            }
        }
    }

    // 뉴스 날짜 포맷을 변경하는 함수
    private fun formatDate(pubDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("yyyy/M/d HH:mm", Locale.ENGLISH)
            val date = inputFormat.parse(pubDate)
            date?.let { outputFormat.format(it) } ?: pubDate
        } catch (e: Exception) {
            pubDate // 변환 실패 시 원본 반환
        }
    }

    private fun fetchNews(query: String) {
        val call = RetrofitClient.instance.searchNews(query)
        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.items?.let { newsList ->
                        // 뉴스 데이터가 충분한 경우에만 UI 업데이트
                        if (newsList.size >= 9) {

                            // 첫 번째 뉴스 (상위 3개)
                            binding.tvNewsAllFirstTitle.text = newsList[0].title
                            binding.tvNewsAllFirstDate.text = formatDate(newsList[0].pubDate)
                            fetchNewsImage(newsList[0].link) { imageUrl ->
                                Glide.with(binding.ivContentNewsAllFirstImage.context)
                                    .load(imageUrl ?: R.drawable.img_news_all_banner_2)
                                    .into(binding.ivContentNewsAllFirstImage)
                            }

                            // 두 번째 뉴스 (다음 3개)
                            binding.tvNewsAllSecondTitle.text = newsList[3].title
                            binding.tvNewsAllSecondDate.text = formatDate(newsList[3].pubDate)
                            fetchNewsImage(newsList[3].link) { imageUrl ->
                                Glide.with(binding.ivContentNewsAllSecondImage.context)
                                    .load(imageUrl ?: R.drawable.img_news_all_banner_2) // 기본 이미지 대체 가능
                                    .into(binding.ivContentNewsAllSecondImage)
                            }

                            // 세 번째 뉴스 (마지막 3개)
                            binding.tvNewsAllThirdTitle.text = newsList[6].title.cleanHtml()
                            binding.tvNewsAllThirdDate.text = formatDate(newsList[6].pubDate)
                            fetchNewsImage(newsList[6].link) { imageUrl ->
                                Glide.with(binding.ivContentNewsAllThirdImage.context)
                                    .load(imageUrl ?: R.drawable.img_news_all_banner_2) // 기본 이미지 대체 가능
                                    .into(binding.ivContentNewsAllThirdImage)
                            }
                        }
                    }
                } else {
                    Log.e("News", "API 호출 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e("News", "네트워크 오류: ${t.message}")
            }
        })
    }

    // 배너화면 설정
    private fun setupHomeBannerUI() {
        val bannerAdapter = NewsAllBannerAdapter(bannerImages)
        binding.vpNewsAllBanner.adapter = bannerAdapter
        // 자동 슬라이드 시작
        handler.postDelayed(autoSlideRunnable, 1500)

        // 사용자가 직접 터치하면 자동 슬라이드 멈추기
        binding.vpNewsAllBanner.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(autoSlideRunnable) // 터치 시 자동 슬라이드 멈춤
                } else if (state == androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE) {
                    handler.postDelayed(autoSlideRunnable, 1500) // 다시 자동 슬라이드 시작
                }
            }
        })
    }

    // 메모리 누수 방지를 위해 onDestroy에서 handler 해제
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoSlideRunnable)
    }
}