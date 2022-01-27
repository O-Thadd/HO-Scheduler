package com.othadd.hoscheduler.viewmodel

import androidx.lifecycle.*
import com.othadd.hoscheduler.database.MonthScheduleDao
import com.othadd.hoscheduler.utils.ScheduleGeneratingHo
import com.othadd.hoscheduler.utils.Scheduler
import kotlinx.coroutines.launch

class GenerateScheduleViewModel(private val monthScheduleDao: MonthScheduleDao) : ViewModel() {

    private val practicalYearRange = 1000..2999

    private var _generateScheduleButtonIsActive = MutableLiveData<Boolean>()
    val generateScheduleButtonIsActive: LiveData<Boolean> get() = _generateScheduleButtonIsActive

    private var _hoListCreateButtonIsActive = MutableLiveData<Boolean>()
    val hoListCreateButtonIsActive: LiveData<Boolean> get() = _hoListCreateButtonIsActive


    private var _hoListIsOk = MutableLiveData<Boolean>()
    val hoListIsOk: LiveData<Boolean> get() = _hoListIsOk

    var scheduleNameIsOk: Boolean
    var monthSelectionIsOk: Boolean
    var yearIsOk: Boolean


    fun setSelectedMonthNumber(selectedMonthNumber: Int) {
        Scheduler.setSelectedMonthNumber(selectedMonthNumber)
    }

    val selectedMonthName: LiveData<String> get() = Scheduler.selectedMonthName
    fun setSelectedMonthName(selectedMonthName: String) {
        Scheduler.setSelectedMonthName(selectedMonthName)
    }

    val scheduleName: LiveData<String> get() = Scheduler.scheduleName
    fun setScheduleName(scheduleName: String) {
        Scheduler.setScheduleName(scheduleName)
    }

    val scheduleYear: LiveData<Int> get() = Scheduler.scheduleYear
    fun setScheduleYear(scheduleYear: Int) {
        Scheduler.setScheduleYear(scheduleYear)
    }

    private val hoList: List<ScheduleGeneratingHo>? get() = Scheduler.newMonthScheduleHoList.value


    fun updateGenerateAndHoListCreateButtonStatus() {
        val yearString = scheduleYear.value.toString()

        yearIsOk =
            if (yearString.isBlank() || yearString == "null") false else yearString.toInt() in practicalYearRange
        monthSelectionIsOk = selectedMonthName.value?.isNotEmpty() ?: false
        scheduleNameIsOk = scheduleName.value?.isNotEmpty() ?: false
        _hoListIsOk.value = hoList?.isNotEmpty() ?: false

        _hoListCreateButtonIsActive.value = monthSelectionIsOk && yearIsOk
        _generateScheduleButtonIsActive.value =
            monthSelectionIsOk && scheduleNameIsOk && yearIsOk && hoListIsOk.value == true

    }

    fun updateDaysInMonthForNewSchedule() {
        Scheduler.updateDaysInMonthForNewSchedule()
    }

    fun generateSchedules() {
        val newSchedule = Scheduler.generateSchedulesInStages()

        viewModelScope.launch {
            monthScheduleDao.insert(newSchedule)
        }
    }

    init {
        scheduleNameIsOk = false
        yearIsOk = false
        _hoListIsOk.value = false
        monthSelectionIsOk = false
    }

}

class GenerateScheduleViewModelFactory(private val monthScheduleDao: MonthScheduleDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenerateScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenerateScheduleViewModel(monthScheduleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}