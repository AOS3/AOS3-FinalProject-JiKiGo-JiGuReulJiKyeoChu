package com.protect.jikigo.ui.home.my_page

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.protect.jikigo.R
import com.protect.jikigo.databinding.FragmentPointHistoryBinding
import com.protect.jikigo.ui.adapter.CalendarDaysAdapter
import com.protect.jikigo.ui.adapter.PointHistoryAdapter
import com.protect.jikigo.ui.extensions.getUserId
import com.protect.jikigo.ui.extensions.statusBarColor
import com.protect.jikigo.ui.viewModel.PointHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days

@AndroidEntryPoint
class PointHistoryFragment : Fragment() {
    private var _binding: FragmentPointHistoryBinding? = null
    private val binding get() = _binding!!
    private val adapter: PointHistoryAdapter by lazy { PointHistoryAdapter() }
    private val calendarAdapter: CalendarDaysAdapter by lazy { CalendarDaysAdapter() }
    private val viewModel: PointHistoryViewModel by viewModels()
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPointHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBar()
        setLayout()
    }

    private fun setStatusBar() {
        requireActivity().statusBarColor(R.color.primary)
    }

    private fun setLayout() {
        onClickToolbar()
        setupWeekendUI()
        recycler()
        calender()
        observe()
        checkData()
    }

    private fun setupWeekendUI() {
        val weekend = resources.getStringArray(R.array.weekend_array)
        weekend.forEach { day ->
            TextView(requireContext()).apply {
                text = day
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 8)
                setTypeface(null, Typeface.BOLD)
                binding.linearWeekendLabel.addView(this)
            }
        }
    }

    // 시작 시 확인할 데이터
    private fun checkData() {
        lifecycleScope.launch {
            userId = requireContext().getUserId() ?: ""
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            viewModel.loadPointData(userId, today)
        }
    }

    private fun onClickToolbar() {
        binding.toolbarPointHistory.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun recycler() {
        binding.recyclerPointHistory.adapter = adapter
        binding.recycler.adapter = calendarAdapter
    }

    private fun observe() {
        binding.apply {
            viewModel.pointData.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            viewModel.nowMonth.observe(viewLifecycleOwner) {
                textView2.text = viewModel.toCalendarDateFormat()
            }
            viewModel.monthOfDays.observe(viewLifecycleOwner) {
                calendarAdapter.submitList(it)
            }
        }
    }

    /*
    달력
     */
    private fun calender() {
        binding.apply {
            button.setOnClickListener {
                viewModel.changeMonth(false)
            }
            button1.setOnClickListener {
                viewModel.changeMonth(true)
            }
            setTodayDate()
        }
    }

    // 날짜를 "20일 화요일" 형식으로 변환하는 함수
    private fun getFormattedDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val days = arrayOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
        val dayOfWeekStr = days[dayOfWeek - 1] // 요일 변환

        return "${dayOfMonth}일 $dayOfWeekStr"
    }

    // 오늘 날짜를 가져와서 텍스트뷰에 설정하는 함수
    private fun setTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val todayDate = getFormattedDate(year, month, day)
        binding.tvPointHistoryDate.text = todayDate

        return todayDate
    }

}

/*
* 0. user의 저장된 Calendar 컬렉션을 모두 가져온 후 리스트에 보여줄 수 있도록 한다.
* 1. 리스트 클릭 시 2025-02-27 형태로 변경
* 2. 2025-02-27 문서, userId를 가지고 내역 데이터 찾기
* 3. 찾은 데이터를 PointHistoryAdapter에 전달
*
* 일단 모델 정의
* 캘린터 리스트 모델 변경
* 값 불러와서 날짜 매치하여 값 넣기
*
* */