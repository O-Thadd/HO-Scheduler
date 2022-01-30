package com.othadd.hoscheduler.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.othadd.hoscheduler.database.MonthSchedule
import java.util.*

object Scheduler {

    private var _newMonthScheduleHoList = MutableLiveData<MutableList<ScheduleGeneratingHo>>()
    val newMonthScheduleHoList: LiveData<MutableList<ScheduleGeneratingHo>> get() = _newMonthScheduleHoList


    private var hoOffDays = mutableListOf<Int>()
    fun updateOffDay(dayNumber: Int) {
        if (hoOffDays.contains(dayNumber))
            hoOffDays.remove(dayNumber)
        else hoOffDays.add(dayNumber)
    }

    private var hoOutsidePostingDays = mutableListOf<Int>()
    fun updateHoOutsidePostingDays(dayNumber: Int) {
        if (hoOutsidePostingDays.contains(dayNumber))
            hoOutsidePostingDays.remove(dayNumber)
        else hoOutsidePostingDays.add(dayNumber)
    }

    fun addScheduleGeneratingHo(name: String, resumptionDate: Int, exitDay: Int) {
        val number = (_newMonthScheduleHoList.value?.size?.plus(1))

        val ho = number?.let { ScheduleGeneratingHo(name, it, resumptionDate, exitDay) }

        ho?.outDays?.addAll(hoOffDays)
        ho?.outSidePostingDays?.addAll(hoOutsidePostingDays)

        ho?.let { _newMonthScheduleHoList.value?.add(it) }

        hoOffDays.clear()
        hoOutsidePostingDays.clear()
    }


    private var selectedMonthNumber = 0
    fun setSelectedMonthNumber(scheduleMonthNumber: Int) {
        selectedMonthNumber = scheduleMonthNumber
    }

    private var _selectedMonthName = MutableLiveData<String>()
    val selectedMonthName: LiveData<String> get() = _selectedMonthName
    fun setSelectedMonthName(selectedMonthName: String) {
        _selectedMonthName.value = selectedMonthName
    }

    private var _scheduleName = MutableLiveData<String>()
    val scheduleName: LiveData<String> get() = _scheduleName
    fun setScheduleName(scheduleName: String) {
        _scheduleName.value = scheduleName
    }

    private var _scheduleYear = MutableLiveData<Int>()
    val scheduleYear: LiveData<Int> get() = _scheduleYear
    fun setScheduleYear(scheduleYear: Int) {
        _scheduleYear.value = scheduleYear
    }

    var daysInMonthForNewSchedule = listOf<Int>()
    fun updateDaysInMonthForNewSchedule() {
        daysInMonthForNewSchedule = (_scheduleYear.value?.let {
            getListOfDays(
                it,
                selectedMonthNumber
            )
        }) as List<Int>
    }


    private fun clearGenerateScheduleFields() {
        _selectedMonthName.value = ""
        _scheduleName.value = ""
        _scheduleYear.value = 0
        _newMonthScheduleHoList.value?.clear()
    }

    fun generateSchedulesInStages(
        scheduleName: String = _scheduleName.value!!,
        monthName: String = _selectedMonthName.value!!,
        monthNumber: Int = selectedMonthNumber,
        year: Int = _scheduleYear.value!!
    ): MonthSchedule {

        val hos = newMonthScheduleHoList.value?.let { ScheduleGeneratingHoContainer(it).asHos() }
        val days = mutableListOf<Day>()
        val wards = mutableListOf<Ward>()
        var minimumIntervalBetweenCalls = 0

        if (hos != null) {
            val numberOfHosAvailableForMuchOfTheMonth =
                hos.count { it.getNumberOfDaysAvailable() >= 23 }
            minimumIntervalBetweenCalls = Math.floorDiv(numberOfHosAvailableForMuchOfTheMonth, 9)
        }

        val allDaysInMonth = getListOfDays(year, monthNumber)
        val weekendDays: MutableList<Int> = mutableListOf()
        val weekdays: MutableList<Int> = mutableListOf()

        for (day in allDaysInMonth) {
            val calendarObjectForThisDay = Calendar.getInstance()
            calendarObjectForThisDay.set(year, monthNumber, day)
            if (calendarObjectForThisDay.get(Calendar.DAY_OF_WEEK) == 1 || (calendarObjectForThisDay.get(
                    Calendar.DAY_OF_WEEK
                ) == 7)
            ) {
                weekendDays.add(day)
            } else weekdays.add(day)
        }

        generateSchedules(
            monthNumber,
            year,
            Ward.ACTIVE_WARDS_STRING,
            weekendDays,
            hos,
            days,
            wards, minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            Ward.ACTIVE_WARDS_STRING,
            weekdays,
            hos,
            days,
            wards,
            minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            Ward.SW4_WARD_STRING,
            weekendDays,
            hos,
            days,
            wards,
            minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            Ward.SW4_WARD_STRING,
            weekdays,
            hos,
            days,
            wards,
            minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            Ward.NON_ACTIVE_WARDS_STRING,
            weekendDays,
            hos,
            days, wards, minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            Ward.NON_ACTIVE_WARDS_STRING,
            weekdays,
            hos,
            days, wards, minimumIntervalBetweenCalls
        )


        val newMonthSchedule = (hos?.let {
            MonthSchedule(
                name = scheduleName,
                hos = it,
                wards = wards,
                days = days,
                month = monthName,
                monthNumber = monthNumber,
                year = year
            )
        })!!

        clearGenerateScheduleFields()

        return newMonthSchedule
    }


    private fun generateSchedules(
        monthNumber: Int,
        year: Int,
        wardList: HashMap<Int, String>,
        dayNumbers: List<Int>,
        hos: List<Ho>?,
        days: MutableList<Day>,
        wards: MutableList<Ward>,
        minimumIntervalBetweenCalls: Int
    ) {


        for (dayNumber in dayNumbers) {
            val calendarObjectForThisDay = Calendar.getInstance()
            calendarObjectForThisDay.set(year, monthNumber, dayNumber)
            val dayOfWeek = Day.getDayOfWeek(dayNumber, monthNumber, year)
            val isWeekend =
                calendarObjectForThisDay.get(Calendar.DAY_OF_WEEK) == 1 || (calendarObjectForThisDay.get(
                    Calendar.DAY_OF_WEEK
                ) == 7)

            for ((wardNumber, wardName) in wardList) {
                val isActiveCall = wardName == Ward.LABOUR_WARD || wardName == Ward.GYNEA_EMERGENCY
                var partnerHasDoneActiveCallBefore = true
                val numberOfHosNeeded = if (isActiveCall) 2 else 1
                for (i in 1..numberOfHosNeeded) {

                    if (isActiveCall) {
                        val partner =
                            hos?.find { ho -> ho.callDaysAndWard.any { it.first == dayNumber && it.second == wardName } }
                        if (partner != null){
                                partnerHasDoneActiveCallBefore = partner.hasDoneActiveCallBefore(dayNumber)
                        }
                    }

                    val ho = hos?.let {
                        pickHo(
                            it,
                            dayNumber,
                            minimumIntervalBetweenCalls,
                            dayOfWeek,
                            partnerHasDoneActiveCallBefore,
                            isActiveCall
                        )
                    }

                    if (ho != null) {

                        ho.callDaysAndWard.add(Pair(dayNumber, wardName))
                        if (isActiveCall) {
                            ho.activeCallDays.add(dayNumber)
                        }
                        if (isWeekend) {
                            ho.weekendCallDays.add(dayNumber)
                        }
                        if (wardName == Ward.SW4) {
                            ho.sw4CallDays.add(dayNumber)
                        }


                        val day = days.find { it.dayNumber == dayNumber }
                        if (day == null) {
//                            internally months go 0 - 11. but for display they should go 1 - 12. so 1 is added to month number.
                            val newDay = Day(dayNumber, dayOfWeek, monthNumber + 1)
                            newDay.wardsAndHosOnCall.add(Pair(wardName, mutableListOf(ho.name)))
                            days.add(newDay)
                        } else {
                            val wardAndHos = day.wardsAndHosOnCall.find { it.first == wardName }
                            if (wardAndHos != null) {
                                wardAndHos.second.add(ho.name)
                            } else {
                                day.wardsAndHosOnCall.add(Pair(wardName, mutableListOf(ho.name)))
                            }
                        }

                        val ward = wards.find { it.number == wardNumber }
                        if (ward == null) {
                            val newWard = Ward(wardName, wardNumber)
                            newWard.daysAndHosOnCall.add(Pair(dayNumber, mutableListOf(ho.name)))
                            wards.add(newWard)
                        } else {
                            val dayAndHos = ward.daysAndHosOnCall.find { it.first == dayNumber }
                            if (dayAndHos != null) {
                                dayAndHos.second.add(ho.name)
                            } else {
                                ward.daysAndHosOnCall.add(Pair(dayNumber, mutableListOf(ho.name)))
                            }
                        }
                    }
                }
            }
        }

    }


    private fun pickHo(
        hoList: List<Ho>,
        day: Int,
        minimumIntervalBetweenCalls: Int,
        dayOfWeek: String,
        partnerIsStillNew: Boolean,
        isActiveCall: Boolean
    ): Ho {

//        picking out HOs that are available
        val availableHos: MutableList<Ho> = mutableListOf()
        for (ho in hoList) {
            if (ho.isAvailable(
                    day,
                    minimumIntervalBetweenCalls,
                    dayOfWeek,
                    partnerIsStillNew,
                    isActiveCall
                )
            ) {
                availableHos.add(ho)
            }
        }

//        if no ho found, then re-run with less tight condition(reducing minimum-interval-between-calls requirement by 1)
        if (availableHos.isEmpty()){
            for (ho in hoList) {
                if (ho.isAvailable(
                        day,
                        minimumIntervalBetweenCalls - 1,
                        dayOfWeek,
                        partnerIsStillNew,
                        isActiveCall
                    )
                ) {
                    availableHos.add(ho)
                }
            }
        }

//        in case no HO is available, then a placeHolder HO('null HO') is returned
        if (availableHos.isEmpty()) {
            return (Ho("null HO", 200))
        }

//        of all available HOs, picking out the one with the least amount of calls.
//        setting the first HO as the one with least calls. Otherwise, there'll be nothing with which to do the 1st comparison

        var hoAvailableWithFewestCalls = availableHos[0]
        for (ho in availableHos) {
            if (ho.callDaysAndWard.size < hoAvailableWithFewestCalls.callDaysAndWard.size) {
                hoAvailableWithFewestCalls = ho
            }
        }


        return hoAvailableWithFewestCalls
    }


    private fun getListOfDays(
        year: Int,
        monthNumber: Int
    ): MutableList<Int> {
        val days = mutableListOf<Int>()
        val calender = Calendar.getInstance()
        calender.set(year, monthNumber, 1)
        val firstDay = calender.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastDay = calender.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in firstDay..lastDay) {
            days.add(day)
        }
        return days
    }

    fun addImportedHos(hos: MutableList<Ho>) {
        _newMonthScheduleHoList.value?.addAll(hos.prepForNewSchedule())
    }

    init {
        _newMonthScheduleHoList.value = mutableListOf()
    }

}