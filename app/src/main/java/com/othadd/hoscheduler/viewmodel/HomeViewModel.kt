package com.othadd.hoscheduler.viewmodel

import androidx.lifecycle.*
import com.othadd.hoscheduler.database.MonthSchedule
import com.othadd.hoscheduler.database.MonthScheduleDao
import kotlinx.coroutines.launch

class HomeViewModel(private val monthScheduleDao: MonthScheduleDao) : ViewModel() {

    private var _monthSchedules = MutableLiveData<List<MonthSchedule>>()
    val monthSchedules: LiveData<List<MonthSchedule>> get() = _monthSchedules


    init {
        viewModelScope.launch {
            _monthSchedules =
                monthScheduleDao.getSchedules().asLiveData() as MutableLiveData<List<MonthSchedule>>
        }
    }
}


class HomeViewModelFactory(private val monthScheduleDao: MonthScheduleDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(monthScheduleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}