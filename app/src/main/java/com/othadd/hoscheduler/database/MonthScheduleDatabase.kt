package com.othadd.hoscheduler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MonthSchedule::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MonthScheduleRoomDatabase : RoomDatabase() {

    abstract fun monthScheduleDao(): MonthScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: MonthScheduleRoomDatabase? = null
        fun getDatabase(context: Context): MonthScheduleRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MonthScheduleRoomDatabase::class.java,
                    "hoScheduler_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}