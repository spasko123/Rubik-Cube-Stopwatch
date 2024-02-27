package com.s.rubiccubestopwatch

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "time_table")
data class TimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val time: String,
    val scramble: String,
    val date: String
)
