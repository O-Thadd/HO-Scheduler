package com.othadd.hoscheduler.viewmodel

import androidx.lifecycle.*
import com.othadd.hoscheduler.database.MonthSchedule
import com.othadd.hoscheduler.database.MonthScheduleDao
import com.othadd.hoscheduler.utils.Ho
import com.othadd.hoscheduler.utils.ScheduleGeneratingHo
import com.othadd.hoscheduler.utils.Scheduler
import kotlinx.coroutines.launch
import java.time.Month

class HoListCreationViewModel(private val monthScheduleDao: MonthScheduleDao) : ViewModel() {

    private var _existingSchedules = MutableLiveData<List<MonthSchedule>>()
    val existingSchedules: LiveData<List<MonthSchedule>> get() = _existingSchedules

    val newMonthScheduleHoList: LiveData<MutableList<ScheduleGeneratingHo>> get() = Scheduler.newMonthScheduleHoList


    fun updateOffDay(dayNumber: Int) {
        Scheduler.updateOffDay(dayNumber)
    }

    fun updateHoOutsidePostingDays(dayNumber: Int) {
        Scheduler.updateHoOutsidePostingDays(dayNumber)
    }

    var daysInMonthForNewSchedule = Scheduler.daysInMonthForNewSchedule


    fun addScheduleGeneratingHo(name: String, resumptionDate: Int = -33, exitDay: Int = 33) {
        Scheduler.addScheduleGeneratingHo(name, resumptionDate, exitDay)
    }

    fun addImportedHos(scheduleId: Int){
        val schedule = _existingSchedules.value?.find { it.id == scheduleId }
        if (schedule != null) {
            Scheduler.addImportedHos(schedule.hos as MutableList<Ho>)
        }
    }

    fun loadSchedules() {
        viewModelScope.launch {
            _existingSchedules = monthScheduleDao.getSchedules().asLiveData() as MutableLiveData<List<MonthSchedule>>
        }
    }

}


class HoListCreationViewModelFactory(private val monthScheduleDao: MonthScheduleDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HoListCreationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HoListCreationViewModel(monthScheduleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}