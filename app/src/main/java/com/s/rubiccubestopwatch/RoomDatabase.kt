package com.s.rubiccubestopwatch

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TimeEntity::class], version = 1, exportSchema = false)
abstract class TimeDatabase : RoomDatabase() {
    abstract fun timeDao(): TimeDao
}
