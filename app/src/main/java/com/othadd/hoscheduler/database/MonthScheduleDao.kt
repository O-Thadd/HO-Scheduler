package com.othadd.hoscheduler.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthScheduleDao {

    @Insert
    suspend fun insert(monthSchedule: MonthSchedule)

    @Delete
    suspend fun delete(monthSchedule: MonthSchedule)

    @Update
    suspend fun update(monthSchedule: MonthSchedule)

    @Query("SELECT * from schedule")
    fun getSchedules(): Flow<List<MonthSchedule>>

    @Query("SELECT * from schedule WHERE id = :monthScheduleId" )
    fun getSchedule(monthScheduleId: Int): MonthSchedule

}