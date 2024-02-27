package com.s.rubiccubestopwatch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TimeDao {
    @Insert
    suspend fun insertData(timeEntity: TimeEntity)

    @Query("SELECT * FROM time_table")
    suspend fun getAllData(): List<TimeEntity>

    @Query("DELETE FROM time_table WHERE id = :timeId")
    suspend fun deleteTimeById(timeId: Long)

    @Query("UPDATE time_table SET time = :newTime WHERE id = :timeId")
    suspend fun updateTimeById(timeId: Long, newTime: String)
}
