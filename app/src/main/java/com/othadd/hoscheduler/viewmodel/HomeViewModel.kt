package com.othadd.hoscheduler.viewmodel

import androidx.lifecycle.*
import com.othadd.hoscheduler.database.MonthSchedule
import com.othadd.hoscheduler.database.MonthScheduleDao
import kotlinx.coroutines.launch

class HomeViewModel(private val monthScheduleDao: MonthScheduleDao) : ViewModel() {

    private var _monthSchedules = MutableLiveData<List<MonthSchedule>>()
    val monthSchedules: LiveData<List<MonthSchedule>> get() = _monthSchedules

    private var _selectionMode = MutableLiveData<Boolean>()
    val selectionMode: LiveData<Boolean> get() = _selectionMode
    fun alterSelectionMode(){
        selectedSchedules.clear()
        _selectionMode.value = !_selectionMode.value!!
    }

//    val uiMonthSchedules get() = monthSchedules.value?.asUiMonthSchedule()

    private val selectedSchedules = mutableListOf<MonthSchedule>()
    fun updateSelectedSchedules(id: Int){
        val selectedSchedule = _monthSchedules.value?.find { it.id == id }
        val scheduleAlreadySelected = selectedSchedules.any { it.id == id }
        if (!scheduleAlreadySelected){
            selectedSchedule?.selected = true
            selectedSchedules.add(selectedSchedule!!)
        } else {
            selectedSchedule?.selected = false
            selectedSchedules.remove(selectedSchedule)
        }
    }

    fun deleteSelectedSchedules(){
        viewModelScope.launch {
            for (schedule in selectedSchedules){
                monthScheduleDao.delete(schedule)
            }
            selectedSchedules.clear()
            _selectionMode.value = false
        }
    }


    init {
        viewModelScope.launch {
            _monthSchedules =
                monthScheduleDao.getSchedules().asLiveData() as MutableLiveData<List<MonthSchedule>>
        }

        _selectionMode.value = false
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