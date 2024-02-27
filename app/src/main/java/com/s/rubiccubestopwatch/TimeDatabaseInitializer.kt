package com.s.rubiccubestopwatch

import android.content.Context
import androidx.room.Room

object TimeDatabaseInitializer {
    private var INSTANCE: TimeDatabase? = null

    fun getInstance(context: Context): TimeDatabase {
        if (INSTANCE == null) {
            synchronized(TimeDatabase::class.java) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    TimeDatabase::class.java,
                    "time_database"
                ).build()
            }
        }
        return INSTANCE!!
    }
}
