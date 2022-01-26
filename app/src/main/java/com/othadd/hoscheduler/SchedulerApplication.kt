package com.othadd.hoscheduler

import android.app.Application
import com.othadd.hoscheduler.database.MonthScheduleRoomDatabase

class SchedulerApplication : Application(){
    val database: MonthScheduleRoomDatabase by lazy { MonthScheduleRoomDatabase.getDatabase(this) }
}