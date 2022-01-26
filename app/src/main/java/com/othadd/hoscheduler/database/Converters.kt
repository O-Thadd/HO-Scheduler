package com.othadd.hoscheduler.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.othadd.hoscheduler.utils.Day
import com.othadd.hoscheduler.utils.Ho
import com.othadd.hoscheduler.utils.Ward
import java.lang.reflect.Type


class Converters {

    @TypeConverter
    fun monthScheduleToString(monthSchedules: List<Int>): String{
        val gson = Gson()
        return gson.toJson(monthSchedules)
    }

    @TypeConverter
    fun stringToMonthSchedule(jsonString: String): List<Int> {
        val gson = Gson()
        val collectionType: Type = object : TypeToken<List<Int?>?>() {}.type
        return gson.fromJson(jsonString, collectionType)
    }

    @TypeConverter
    fun hosToString(hos: List<Ho>): String{
        val gson = Gson()
        return gson.toJson(hos)
    }

    @TypeConverter
    fun stringToHos(jsonString: String): List<Ho> {
        val gson = Gson()
        val collectionType: Type = object : TypeToken<List<Ho?>?>() {}.type
        return gson.fromJson(jsonString, collectionType)
    }

    @TypeConverter
    fun wardsToString(wards: List<Ward>): String{
        val gson = Gson()
        return gson.toJson(wards)
    }

    @TypeConverter
    fun stringToWards(jsonString: String): List<Ward> {
        val gson = Gson()
        val collectionType: Type = object : TypeToken<List<Ward?>?>() {}.type
        return gson.fromJson(jsonString, collectionType)
    }

    @TypeConverter
    fun daysToString(days: List<Day>): String{
        val gson = Gson()
        return gson.toJson(days)
    }

    @TypeConverter
    fun stringToDays(jsonString: String): List<Day> {
        val gson = Gson()
        val collectionType: Type = object : TypeToken<List<Day?>?>() {}.type
        return gson.fromJson(jsonString, collectionType)
    }
}