package com.othadd.hoscheduler.viewmodel

import androidx.lifecycle.*
import com.othadd.hoscheduler.database.MonthSchedule
import com.othadd.hoscheduler.database.MonthScheduleDao
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter.DataItem.*
import com.othadd.hoscheduler.utils.*
import com.othadd.hoscheduler.utils.Ward.Companion.ACTIVE_WARDS_STRING
import com.othadd.hoscheduler.utils.Ward.Companion.GYNEA_EMERGENCY
import com.othadd.hoscheduler.utils.Ward.Companion.LABOUR_WARD
import com.othadd.hoscheduler.utils.Ward.Companion.NON_ACTIVE_WARDS_STRING
import com.othadd.hoscheduler.utils.Ward.Companion.SW4_WARD_STRING
import com.othadd.hoscheduler.utils.Ward.Companion.WARDS_STRING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class SchedulerViewModel(private val monthScheduleDao: MonthScheduleDao) : ViewModel() {

    private var _hos = MutableLiveData<List<Ho>>()
    val hos: LiveData<List<Ho>> get() = _hos

    private var _ho = MutableLiveData<Ho>()
    val ho: LiveData<Ho> get() = _ho

    private var _wards = MutableLiveData<List<Ward>>()
    val wards: LiveData<List<Ward>> get() = _wards

    private var _ward = MutableLiveData<Ward>()
    val ward: LiveData<Ward> get() = _ward

    private var _days = MutableLiveData<List<Day>>()
    val days: LiveData<List<Day>> get() = _days

    private var _day = MutableLiveData<Day>()
    val day: LiveData<Day> get() = _day


    private var _overviewWardItems = MutableLiveData<List<WardItem>>()
    val overviewWardItems: LiveData<List<WardItem>> get() = _overviewWardItems

    private var _overviewDayItems = MutableLiveData<List<DayItem>>()
    val overviewDayItems: LiveData<List<DayItem>> get() = _overviewDayItems

    private var _overviewHoItems = MutableLiveData<List<HoItem>>()
    val overviewHoItems: LiveData<List<HoItem>> get() = _overviewHoItems

    private var _uiHos = MutableLiveData<List<UIHo>>()
    val uiHos: LiveData<List<UIHo>> get() = _uiHos




    private var _newMonthScheduleHoList = MutableLiveData<MutableList<ScheduleGeneratingHo>>()
    val newMonthScheduleHoList: LiveData<MutableList<ScheduleGeneratingHo>> get() = _newMonthScheduleHoList


    private var _generateScheduleButtonIsActive = MutableLiveData<Boolean>()
    val generateScheduleButtonIsActive: LiveData<Boolean> get() = _generateScheduleButtonIsActive

    private var _hoListCreateButtonIsActive = MutableLiveData<Boolean>()
    val hoListCreateButtonIsActive: LiveData<Boolean> get() = _hoListCreateButtonIsActive

    var scheduleNameIsOk = false
    var yearIsOk = false
    var hoListIsOk = false
    var monthSelectionIsOk = false

//    private var _monthSelection = MutableLiveData<String>()
//    val monthSelection: LiveData<String> get() = _monthSelection
//    fun setMonthSelection(month: String) {
//        _monthSelection.value = month
//    }

    var selectedMonthNumber = 0
    var selectedMonth = ""

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

    private fun clearGenerateScheduleFields() {
        selectedMonth = ""
        _scheduleName.value = ""
        _scheduleYear.value = 0
        _newMonthScheduleHoList.value?.clear()
    }


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

    var daysInMonthForNewSchedule = listOf<Int>()
    fun updateDaysInMonthForNewSchedule() {
        daysInMonthForNewSchedule = (_scheduleYear.value?.let { getListOfDays(it, selectedMonthNumber) }) as List<Int>
    }

    fun updateGenerateAndHoListCreateButtonStatus() {
        hoListIsOk = _newMonthScheduleHoList.value?.isNotEmpty() == true
        _generateScheduleButtonIsActive.value = monthSelectionIsOk && scheduleNameIsOk && yearIsOk && hoListIsOk == true
        _hoListCreateButtonIsActive.value = monthSelectionIsOk && yearIsOk
    }


    fun addScheduleGeneratingHo(name: String, resumptionDate: Int = -33, exitDay: Int = 33) {
        val number = (_newMonthScheduleHoList.value?.size?.plus(1))

        val ho = number?.let { ScheduleGeneratingHo(name, it, resumptionDate, exitDay) }

        ho?.outDays?.addAll(hoOffDays)
        ho?.outSidePostingDays?.addAll(hoOutsidePostingDays)

        ho?.let { _newMonthScheduleHoList.value?.add(it) }

        hoOffDays.clear()
        hoOutsidePostingDays.clear()
    }


    private var _monthSchedules = MutableLiveData<List<MonthSchedule>>()
    val monthSchedules: LiveData<List<MonthSchedule>> get() = _monthSchedules

    private var _singleMonthSchedule = MutableLiveData<MonthSchedule>()
    private val singleMonthSchedule: LiveData<MonthSchedule> get() = _singleMonthSchedule

    val monthNumber: Int? get() = _singleMonthSchedule.value?.monthNumber
    val year: Int? get() = singleMonthSchedule.value?.year
    private var minimumIntervalBetweenCalls = 0


    fun setHo(hoNumber: Int) {
        _ho.value = hos.value?.find { it.number == hoNumber }
    }

    fun setWard(wardNumber: Int) {
        _ward.value = wards.value?.find { it.number == wardNumber }
    }

    fun setDay(dayNumber: Int) {
        _day.value = days.value?.find { it.dayNumber == dayNumber }
    }


    fun generateSchedulesInStages(
        scheduleName: String = _scheduleName.value!!,
        monthName: String = selectedMonth,
        monthNumber: Int = selectedMonthNumber,
        year: Int = scheduleYear.value!!
    ) {

        val hos = newMonthScheduleHoList.value?.let { ScheduleGeneratingHoContainer(it).asHos() }
        val days = mutableListOf<Day>()
        val wards = mutableListOf<Ward>()

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
            ACTIVE_WARDS_STRING,
            weekendDays,
            hos,
            days,
            wards, minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            ACTIVE_WARDS_STRING,
            weekdays,
            hos,
            days,
            wards,
            minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            SW4_WARD_STRING,
            weekendDays,
            hos,
            days,
            wards,
            minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            SW4_WARD_STRING,
            weekdays,
            hos,
            days,
            wards,
            minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            NON_ACTIVE_WARDS_STRING,
            weekendDays,
            hos,
            days, wards, minimumIntervalBetweenCalls
        )
        generateSchedules(
            monthNumber,
            year,
            NON_ACTIVE_WARDS_STRING,
            weekdays,
            hos,
            days, wards, minimumIntervalBetweenCalls
        )


        val newMonthSchedule = hos?.let {
            MonthSchedule(
                name = scheduleName,
                hos = it,
                wards = wards,
                days = days,
                month = monthName,
                monthNumber = monthNumber,
                year = year
            )
        }

        viewModelScope.launch {
            if (newMonthSchedule != null) {
                monthScheduleDao.insert(newMonthSchedule)
            }
        }

        clearGenerateScheduleFields()
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

//        val hos = (newMonthScheduleHoList.value)
//        val dayNumbers = getListOfDays(year, monthNumber)

        for (dayNumber in dayNumbers) {
            val calendarObjectForThisDay = Calendar.getInstance()
            calendarObjectForThisDay.set(year, monthNumber, dayNumber)
            val dayOfWeek = Day.getDayOfWeek(dayNumber, monthNumber, year)
            val isWeekend =
                calendarObjectForThisDay.get(Calendar.DAY_OF_WEEK) == 1 || (calendarObjectForThisDay.get(
                    Calendar.DAY_OF_WEEK
                ) == 7)

            for ((wardNumber, wardName) in wardList) {
                val isActiveCall = wardName == LABOUR_WARD || wardName == GYNEA_EMERGENCY
                var partnerIsStillNew = false
                val numberOfHosNeeded = if (isActiveCall) 2 else 1
                for (i in 1..numberOfHosNeeded) {

                    if (isActiveCall) {
                        val partner =
                            hos?.find { ho -> ho.callDaysAndWard.any { it.first == dayNumber && it.second == wardName } }
                        if (partner != null && partner.isStillNew()) {
                            partnerIsStillNew = true
                        }
                    }

                    val ho = hos?.let {
                        new1PickHo(
                            it,
                            dayNumber,
                            minimumIntervalBetweenCalls,
                            dayOfWeek,
                            partnerIsStillNew,
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


    private fun new1PickHo(
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

//        of all available HOs, picking out the one with the least amount of calls.
//        setting the first HO as the one with least calls. Otherwise, there'll be nothing with which to do the 1st comparison

        if(availableHos.isEmpty()){
            return (Ho("null HO", 200))
        }

        var hoAvailableWithFewestCalls = availableHos[0]
        for (ho in availableHos) {
            if (ho.callDaysAndWard.size < hoAvailableWithFewestCalls.callDaysAndWard.size) {
                hoAvailableWithFewestCalls = ho
            }
        }


        return hoAvailableWithFewestCalls
    }


    private fun newPickHo(
        hoNumberLastPicked: Int,
        hoList: List<Ho>,
        day: Int,
        minimumIntervalBetweenCalls: Int,
        dayOfWeek: String,
        partnerIsStillNew: Boolean,
        isActiveCall: Boolean
    ): Ho {

        var gottenHo = false
        var index = if (hoNumberLastPicked == hoList.last().number) 1 else hoNumberLastPicked + 1
        lateinit var ho: Ho

        while (!gottenHo) {
            ho = (hoList.find { it.number == index })!!
            if (ho.isAvailable(
                    day,
                    minimumIntervalBetweenCalls,
                    dayOfWeek,
                    partnerIsStillNew,
                    isActiveCall
                )
            ) {
                gottenHo = true
            } else index = if (index == hoList.last().number) 1 else index + 1
        }
        return ho
    }

    fun loadSingleMonthSchedule(monthScheduleId: Int) {

        viewModelScope.launch {

            val result = getSingleMonthFromDataBase(monthScheduleId)

            _singleMonthSchedule.value = result

            _hos.value = _singleMonthSchedule.value?.hos
            _days.value = _singleMonthSchedule.value?.days
            _wards.value = _singleMonthSchedule.value?.wards

            _overviewWardItems.value = Ward.getOverviewWardItems()

            val daysContainer = _days.value?.let { DaysContainer(it) }

            _overviewDayItems.value = daysContainer?.asOverviewDayItems()?.sortedBy { it.dayNumber }
            _overviewHoItems.value = daysContainer?.asOverviewHoItems()

            _uiHos.value = _hos.value?.let { HosContainer(it).asUIHos() }
        }

    }

    private suspend fun getSingleMonthFromDataBase(monthScheduleId: Int): MonthSchedule {
        return withContext(Dispatchers.IO) {
            monthScheduleDao.getSchedule(monthScheduleId)
        }
    }

    private fun wardHasDayAlready(ward: Ward, day: Int): Boolean {
        return ward.daysAndHosOnCall.any { it.first == day }
    }

    private fun dayHasWardAlready(day: Day, wardNumber: Int): Boolean {
        return day.wardsAndHosOnCall.any { it.first == WARDS_STRING[wardNumber] }
    }

    init {
        viewModelScope.launch {
            _monthSchedules =
                monthScheduleDao.getSchedules().asLiveData() as MutableLiveData<List<MonthSchedule>>
        }

        _newMonthScheduleHoList.value = mutableListOf()

        selectedMonth = ""
        _scheduleName.value = ""
//        _scheduleYear.value = 0
        _generateScheduleButtonIsActive.value = false
        _hoListCreateButtonIsActive.value = false

//        _overviewDayItems.value = mutableListOf()
//        _overviewHoItems.value = mutableListOf()

        _hos.value = mutableListOf()
        _wards.value = mutableListOf()
        _days.value = mutableListOf()

    }
}

class SchedulerViewModelFactory(private val monthScheduleDao: MonthScheduleDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchedulerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SchedulerViewModel(monthScheduleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}