package com.othadd.hoscheduler.viewmodel

import androidx.lifecycle.*
import com.othadd.hoscheduler.database.MonthSchedule
import com.othadd.hoscheduler.database.MonthScheduleDao
import com.othadd.hoscheduler.utils.Ho
import com.othadd.hoscheduler.utils.ScheduleGeneratingHo
import com.othadd.hoscheduler.utils.Scheduler
import kotlinx.coroutines.launch

class HoListCreationViewModel(private val monthScheduleDao: MonthScheduleDao) : ViewModel() {

    val ho: LiveData<ScheduleGeneratingHo?> get() = Scheduler.ho

    private var _existingSchedules = MutableLiveData<List<MonthSchedule>>()
    val existingSchedules: LiveData<List<MonthSchedule>> get() = _existingSchedules

    val newMonthScheduleHoList: LiveData<MutableList<ScheduleGeneratingHo>> get() = Scheduler.newMonthScheduleHoList

    var daysInMonthForNewSchedule = Scheduler.daysInMonthForNewSchedule

    var updatingHo = false

    fun updateOffDay(dayNumber: Int) {
        Scheduler.updateOffDay(dayNumber)
    }

    fun updateHoOutsidePostingDays(dayNumber: Int) {
        Scheduler.updateHoOutsidePostingDays(dayNumber)
    }

    fun updateScheduleGeneratingHoList(name: String, resumptionDate: Int = -33, exitDay: Int = 33){
        Scheduler.addNewScheduleGeneratingHo(name, resumptionDate, exitDay)
    }

    fun updateScheduleGeneratingHoList(hoNumber: Int, name: String, resumptionDate: Int = -33, exitDay: Int = 33){
        Scheduler.updateScheduleGeneratingHo(hoNumber, name, resumptionDate, exitDay)
    }

    fun setHo(hoNumber: Int){
        Scheduler.setHo(hoNumber)
    }

    fun clearSetHo(){
        Scheduler.clearSetHo()
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