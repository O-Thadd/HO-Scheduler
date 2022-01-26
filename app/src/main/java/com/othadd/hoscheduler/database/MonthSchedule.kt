package com.othadd.hoscheduler.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.othadd.hoscheduler.utils.Day
import com.othadd.hoscheduler.utils.Ho
import com.othadd.hoscheduler.utils.Ward

@Entity(tableName = "schedule")
data class MonthSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val hos: List<Ho>,
    @ColumnInfo val wards: List<Ward>,
    @ColumnInfo val days: List<Day>,
    @ColumnInfo val month: String,
    @ColumnInfo val monthNumber: Int,
    @ColumnInfo val year: Int
)
