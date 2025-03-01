package com.protect.jikigo.ui.viewModel

import android.util.Log
import androidx.datastore.dataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protect.jikigo.data.model.UserPaymentHistory
import com.protect.jikigo.data.repo.PointHistoryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PointHistoryViewModel @Inject constructor(
    private val pointHistoryRepo: PointHistoryRepo
) : ViewModel() {

    private val _pointData = MutableLiveData<MutableList<UserPaymentHistory>>()
    val pointData: LiveData<MutableList<UserPaymentHistory>> get() = _pointData

    private val _nowYear = MutableLiveData<Int>()
    val nowYear: LiveData<Int> get() = _nowYear

    private val _nowMonth = MutableLiveData<Int>()
    val nowMonth: LiveData<Int> get() = _nowMonth

    private val _monthOfDays = MutableLiveData<List<Int>>()
    val monthOfDays: LiveData<List<Int>> get() = _monthOfDays

    private val _checkedDataList = MutableLiveData<List<String>>()
    val checkedDataList: LiveData<List<String>> get() = _checkedDataList

    init {
        getNowYearAndMonth()
        getDateOfTheCurrentMonth()

        pointHistoryRepo.loadCalendar("wltjr6432@naver.com", nowYear.value!!, nowMonth.value!!) {
            _checkedDataList.value = it
            getCalenderData()
        }
    }


    // 현재 년 월을 받아오기
    private fun getNowYearAndMonth() {
        Calendar.getInstance().apply {
            _nowYear.value = get(Calendar.YEAR)
            _nowMonth.value = get(Calendar.MONTH) + 1
        }
    }

    // 2025-05-01
    fun loadPointData(userId: String, selectDate: String) {
        pointHistoryRepo.loadPointHistory(userId, selectDate) {
            _pointData.value = it
        }
    }

    fun changeMonth(boolean: Boolean) {
        // true: 다음 월, false: 이전 월
        if (boolean) {
            if (_nowMonth.value!! == 12) {
                _nowYear.value = _nowYear.value?.plus(1)
                _nowMonth.value = 1
            } else {
                _nowMonth.value = _nowMonth.value?.plus(1)
            }
        } else {
            if (_nowMonth.value!! == 1) {
                _nowYear.value = _nowYear.value?.minus(1)
                _nowMonth.value = 12
            } else {
                _nowMonth.value = _nowMonth.value?.minus(1)
            }
        }
        getDateOfTheCurrentMonth()
    }


    private fun getDateOfTheCurrentMonth() {
        val calendar = Calendar.getInstance().apply {
            set(_nowYear.value!!, _nowMonth.value!! - 1, 1)
        }

        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val firstDay = calendar.get(Calendar.DAY_OF_WEEK)
        val emptyDays = firstDay - Calendar.SUNDAY


        _monthOfDays.value = mutableListOf<Int>().apply {
            for (i in 1..emptyDays) {
                add(0)
            }

            for (i in 1..lastDayOfMonth) {
                add(i)
            }
        }
    }

    fun getCalenderData() {
        val calendarList = mutableListOf<String>()
        _monthOfDays.value!!.forEach {
            val data = String.format("%04d-%02d-%02d", _nowYear.value, _nowMonth.value, it)
            calendarList.add(data)
        }

        val checkedList = calendarList.map {
            _checkedDataList.value!!.contains(it)
        }

        Log.d("test", checkedList.toString())
    }

    fun toCalendarDateFormat(): String = String.format("%04d-%02d", _nowYear.value, _nowMonth.value)
}