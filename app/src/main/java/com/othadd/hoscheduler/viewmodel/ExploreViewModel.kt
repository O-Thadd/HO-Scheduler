package com.othadd.hoscheduler.viewmodel

import androidx.lifecycle.*
import com.othadd.hoscheduler.database.MonthSchedule
import com.othadd.hoscheduler.database.MonthScheduleDao
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter.DataItem
import com.othadd.hoscheduler.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverviewViewModel(private val monthScheduleDao: MonthScheduleDao) : ViewModel() {

    private var _hos = MutableLiveData<List<Ho>>()
    val hos: LiveData<List<Ho>> get() = _hos

    private var _ho = MutableLiveData<Ho>()
    val ho: LiveData<Ho> get() = _ho

    private var _wards = MutableLiveData<List<Ward>>()
    private val wards: LiveData<List<Ward>> get() = _wards

    private var _ward = MutableLiveData<Ward>()
    val ward: LiveData<Ward> get() = _ward

    private var _days = MutableLiveData<List<Day>>()
    val days: LiveData<List<Day>> get() = _days

    private var _day = MutableLiveData<Day>()
    val day: LiveData<Day> get() = _day

    private var _stillLoadingSchedule = MutableLiveData<Boolean>()
    val stillLoadingSchedule: LiveData<Boolean> get() = _stillLoadingSchedule


    private var _overviewWardItems = MutableLiveData<List<DataItem.WardItem>>()
    val overviewWardItems: LiveData<List<DataItem.WardItem>> get() = _overviewWardItems

    private var _overviewDayItems = MutableLiveData<List<DataItem.DayItem>>()
    val overviewDayItems: LiveData<List<DataItem.DayItem>> get() = _overviewDayItems

    private var _overviewHoItems = MutableLiveData<List<DataItem.HoItem>>()
    val overviewHoItems: LiveData<List<DataItem.HoItem>> get() = _overviewHoItems

    private var _uiHos = MutableLiveData<List<UIHo>>()
    val uiHos: LiveData<List<UIHo>> get() = _uiHos

    private var _singleMonthSchedule = MutableLiveData<MonthSchedule>()
    private val singleMonthSchedule: LiveData<MonthSchedule> get() = _singleMonthSchedule

    val monthNumber: Int? get() = _singleMonthSchedule.value?.monthNumber
    val year: Int? get() = singleMonthSchedule.value?.year

    fun setHo(hoNumber: Int) {
        _ho.value = hos.value?.find { it.number == hoNumber }
    }

    fun setWard(wardNumber: Int) {
        _ward.value = wards.value?.find { it.number == wardNumber }
    }

    fun setDay(dayNumber: Int) {
        _day.value = days.value?.find { it.dayNumber == dayNumber }
    }


    fun loadMonthSchedule(monthScheduleId: Int){

        viewModelScope.launch {

            _stillLoadingSchedule.value = true

            val monthSchedule = getMonthScheduleFromDataBase(monthScheduleId)

            _singleMonthSchedule.value = monthSchedule

            monthSchedule.let {
                _hos.value = it.hos
                _wards.value = it.wards
                _days.value = it.days
            }

            _overviewWardItems.value = Ward.getOverviewWardItems()

            val daysContainer = _days.value?.let { DaysContainer(it) }

            _overviewDayItems.value = daysContainer?.asOverviewDayItems()?.sortedBy { it.dayNumber }
            _overviewHoItems.value = daysContainer?.asOverviewHoItems()

            _uiHos.value = _hos.value?.let { HosContainer(it).asUIHos() }

            _stillLoadingSchedule.value = false

        }


    }

    private suspend fun getMonthScheduleFromDataBase(monthScheduleId: Int): MonthSchedule {
        return withContext(Dispatchers.IO) {
            monthScheduleDao.getSchedule(monthScheduleId)
        }
    }

}


class OverviewViewModelFactory(private val monthScheduleDao: MonthScheduleDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OverviewViewModel(monthScheduleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}