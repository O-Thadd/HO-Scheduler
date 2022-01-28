package com.othadd.hoscheduler.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.othadd.hoscheduler.database.MonthScheduleDao
import com.othadd.hoscheduler.utils.ScheduleGeneratingHo
import com.othadd.hoscheduler.utils.Scheduler
import java.util.*

class HoListCreationViewModel(private val monthScheduleDao: MonthScheduleDao) : ViewModel() {

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