package com.othadd.hoscheduler.utils

import android.util.Log
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter.DataItem.*
import java.util.*
import java.util.Collections.max
import kotlin.collections.HashMap

data class Ho(
    val name: String,
    val number: Int,
    val outDays: List<Int> = listOf(),
    val resumptionDay: Int = -33,
    val endDay: Int = 33,
    val activeCallDays: MutableList<Int> = mutableListOf(),
    val weekendCallDays: MutableList<Int> = mutableListOf(),
    val sw4CallDays: MutableList<Int> = mutableListOf(),
    val outSidePostingDays: List<Int> = listOf(),
    val callDaysAndWard: MutableList<Pair<Int, String?>> = mutableListOf()
) {
    class HoDetail(val dayName: String, val dayNumber: Int, val monthNumber: Int, val ward: String)

    fun getListOfDetails(monthNumber: Int, year: Int): List<HoDetail> {
        val list = mutableListOf<HoDetail>()
        for ((day, ward) in callDaysAndWard) {
            val dayName = Day.getDayOfWeek(day, monthNumber, year)

//            monthNumber here will be displayed, so adjust accordingly (+1)
            ward?.let { HoDetail(dayName, day, monthNumber + 1, it) }?.let { list.add(it) }
        }
        return list.sortedBy { it.dayNumber }
    }


    private val typicalNumberOfDaysInMonth = 30

    fun getNumberOfDaysAvailable(): Int {
        return typicalNumberOfDaysInMonth - outDays.size
    }

    fun isStillNew(): Boolean {
        return resumptionDay != -33 && activeCallDays.isEmpty()
    }


    fun isAvailable(
        day: Int,
        minimumIntervalBetweenCalls: Int,
        dayOfWeek: String,
        partnerIsStillNew: Boolean,
        isActiveCall: Boolean
    ): Boolean {
        val enoughBreakBetweenLastAndNextCall = enoughBreakBetweenLastAndNextCall(day, minimumIntervalBetweenCalls)
        val notDuringOutDay = !(outDays.any { it == day })
        var noOutsidePostingActiveCallIncompatibility = true
        val currentlyOnOutsidePosting = outSidePostingDays.any { it == day }
        val isTuesdayOrFriday = dayOfWeek == Day.TUESDAY || dayOfWeek == Day.FRIDAY
        if (currentlyOnOutsidePosting && isActiveCall && !isTuesdayOrFriday) {
            noOutsidePostingActiveCallIncompatibility = false
        }
        val isNotWithinThreeDaysOfResumption = day - resumptionDay > 2
        val isNotWithinFiveDaysOfResumption = day - resumptionDay > 4
        var isNotTooCloseToResumptionDay = true
        if (isActiveCall && !isNotWithinFiveDaysOfResumption) {
            isNotTooCloseToResumptionDay = false
        }
        if (!isActiveCall && !isNotWithinThreeDaysOfResumption) {
            isNotTooCloseToResumptionDay = false
        }
        var bothPartnersNotStillNew = true
        if(isActiveCall && isStillNew() && partnerIsStillNew)
            bothPartnersNotStillNew = false
        val notYetExitDay = day <= endDay

        val availability =
            enoughBreakBetweenLastAndNextCall && notDuringOutDay && noOutsidePostingActiveCallIncompatibility && isNotTooCloseToResumptionDay && bothPartnersNotStillNew && notYetExitDay

//        if(!availability) {
//            Log.e(
//                "my message",
//                "enough break between last and next call is $enoughBreakBetweenLastAndNextCall, not during out day is $notDuringOutDay, no outside posting active call incompatibility is $noOutsidePostingActiveCallIncompatibility, in not too close to resumptionday is $isNotTooCloseToResumptionDay, both patners not still new is $bothPartnersNotStillNew, not yet exit day is $notYetExitDay"
//            )
//        }

        return availability
    }

    private fun getLastCallDay(): Int {
        val callDays = mutableListOf<Int>()

        for ((day, _) in callDaysAndWard) {
            callDays.add(day)
        }

        return max(callDays)
    }

    private fun enoughBreakBetweenLastAndNextCall(dayNumber: Int, minimumIntervalBetweenCalls: Int): Boolean{

        if (callDaysAndWard.isEmpty())
            return true

        val allPreviousCallDays = mutableListOf<Int>()
        val allFollowingCallDays = mutableListOf<Int>()

        for ((day, _) in callDaysAndWard){
            if(day < dayNumber){
                allPreviousCallDays.add(day)
            }
            else if(day > dayNumber){
                allFollowingCallDays.add(day)
            }
            else {
                return false
            }
        }

        val enoughBreakSinceLastCall: Boolean
        val enoughBreakUntilNextCall: Boolean

        val mostRecentCallDay = allPreviousCallDays.maxOrNull()
        val followingCallDay = allFollowingCallDays.minOrNull()

        enoughBreakSinceLastCall = if (mostRecentCallDay != null){
            val daysSinceLastCall = dayNumber - mostRecentCallDay
            daysSinceLastCall >= minimumIntervalBetweenCalls
        } else {
            true
        }

        enoughBreakUntilNextCall = if (followingCallDay != null){
            val daysUntilNextCall = followingCallDay - dayNumber
            daysUntilNextCall >= minimumIntervalBetweenCalls
        } else {
            true
        }

        return enoughBreakSinceLastCall && enoughBreakUntilNextCall
    }

}

data class Ward(
    val name: String,
    val number: Int,
    val daysAndHosOnCall: MutableList<Pair<Int, MutableList<String>>> = mutableListOf()
) {

    class WardDetail(
        val dayNumber: Int,
        val monthNumber: Int,
        val dayName: String,
        val ho1: String,
        val ho2: String
    )

    fun getListOfDetails(monthNumber: Int, year: Int): List<WardDetail> {
        val list = mutableListOf<WardDetail>()
        for ((day, hos) in daysAndHosOnCall) {
            val dayName = Day.getDayOfWeek(day, monthNumber, year)
            val ho1 = hos[0]
            val ho2 = if (hos.size == 1) "" else hos[1]
            list.add(WardDetail(day, monthNumber, dayName, ho1, ho2))
        }
        return list.sortedBy { it.dayNumber }
    }


    companion object {

        const val LABOUR_WARD = "Labour Ward"
        const val GYNEA_EMERGENCY = "Gynea Emergency"
        const val C14TH = "C14th"
        const val SE4 = "SE4"
        const val SW4 = "SW4"
        const val W4 = "W4"
        const val WW3 = "WW3"

        val WARDS = listOf(LABOUR_WARD, GYNEA_EMERGENCY, C14TH, SE4, SW4, W4, WW3)

        val WARDS_STRING: HashMap<Int, String> = hashMapOf(
            1 to LABOUR_WARD,
            2 to GYNEA_EMERGENCY,
            3 to C14TH,
            4 to SE4,
            5 to SW4,
            6 to W4,
            7 to WW3
        )

        val ACTIVE_WARDS_STRING: HashMap<Int, String> = hashMapOf(
            1 to LABOUR_WARD,
            2 to GYNEA_EMERGENCY
        )

        val SW4_WARD_STRING: HashMap<Int, String> = hashMapOf(
            5 to SW4
        )

        val NON_ACTIVE_WARDS_STRING: HashMap<Int, String> = hashMapOf(
            3 to C14TH,
            4 to SE4,
            6 to W4,
            7 to WW3
        )

        val WARDS_SHORT_STRING: HashMap<Int, String> = hashMapOf(
            1 to "L/W",
            2 to "G/E",
            3 to "C14",
            4 to SE4,
            5 to SW4,
            6 to W4,
            7 to WW3
        )

        fun getOverviewWardItems(): List<WardItem> {
            val list = mutableListOf<WardItem>()
            for ((wardNumber, wardShortName) in WARDS_SHORT_STRING) {
                val wardName = WARDS_STRING[wardNumber]
                wardName?.let { WardItem(wardNumber, it, wardShortName) }?.let { list.add(it) }
            }
            return list
        }
    }
}

data class Day(
    val dayNumber: Int,
    val dayName: String,
    val monthNumber: Int,
    val wardsAndHosOnCall: MutableList<Pair<String, MutableList<String>>> = mutableListOf()
) {

    class DayDetail(val ward: String, val ho1: String, val ho2: String)

    fun getListOfDetails(): List<DayDetail> {
        val list = mutableListOf<DayDetail>()
        for ((ward, hos) in wardsAndHosOnCall) {
            val ho1 = hos[0]
            val ho2 = if (hos.size == 1) "" else hos[1]
            list.add(DayDetail(ward, ho1, ho2))
        }
        return list
    }

    fun getShortDayOfWeek(): String {
        return when (dayName) {
            SUNDAY -> "Sun"
            MONDAY -> "Mon"
            TUESDAY -> "Tue"
            WEDNESDAY -> "Wed"
            THURSDAY -> "Thu"
            FRIDAY -> "Fri"
            else -> "Sat"
        }
    }

    companion object {

        const val MONDAY = "Monday"
        const val TUESDAY = "Tuesday"
        const val WEDNESDAY = "Wednesday"
        const val THURSDAY = "Thursday"
        const val FRIDAY = "Friday"
        const val SATURDAY = "Saturday"
        const val SUNDAY = "Sunday"

        fun getDayOfWeek(dayNumber: Int, monthNumber: Int, year: Int): String {

            val date = Calendar.getInstance()
            date.set(year, monthNumber, dayNumber)

            return when (date.get(Calendar.DAY_OF_WEEK)) {
                1 -> SUNDAY
                2 -> MONDAY
                3 -> TUESDAY
                4 -> WEDNESDAY
                5 -> THURSDAY
                6 -> FRIDAY
                else -> SATURDAY
            }

        }

    }

}

data class ScheduleGeneratingHo(
    val name: String,
    val number: Int,
    val resumptionDay: Int,
    val exitDay: Int,
    val outDays: MutableList<Int> = mutableListOf(),
    val outSidePostingDays: MutableList<Int> = mutableListOf()
)

class ScheduleGeneratingHoContainer(val scheduleGeneratingHos: List<ScheduleGeneratingHo>)

fun ScheduleGeneratingHoContainer.asHos(): List<Ho> {
    return scheduleGeneratingHos.map {
        Ho(
            name = it.name,
            number = it.number,
            resumptionDay = it.resumptionDay,
            endDay = it.exitDay,
            outDays = it.outDays,
            outSidePostingDays = it.outSidePostingDays
        )
    }
}

data class UIHo(
    val number: Int,
    val name: String,
    val noOfCalls: Int,
    val noOfActiveCalls: Int,
    val noOfSW4Calls: Int,
    val noOfWeekendCalls: Int
)

class HosContainer(val hos: List<Ho>)

fun HosContainer.asUIHos(): List<UIHo> {
    return hos.map { ho ->
        UIHo(
            ho.number,
            ho.name,
            ho.callDaysAndWard.size,
            ho.activeCallDays.size,
            ho.sw4CallDays.size,
            ho.weekendCallDays.size
        )
    }
}

class DaysContainer(val days: List<Day>)

fun DaysContainer.asOverviewDayItems(): List<DayItem> {
    return days.map { day ->
        DayItem(day.dayNumber, day.monthNumber, day.getShortDayOfWeek())
    }
}

fun DaysContainer.asOverviewHoItems(): List<HoItem> {
    val hoItems = mutableListOf<HoItem>()
    for (day in days) {
        for (wardAndHO in day.wardsAndHosOnCall) {
            hoItems.add(HoItem(day.dayNumber, wardAndHO.first, wardAndHO.second))
        }
    }
    return hoItems
}