package com.allybros.elephant_todo_app.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by orcun on 6.07.2022
 */

@Entity
data class Item(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "note") val note: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "time") val time: String?,
    @ColumnInfo(name = "is_complete") val isComplete: Boolean?,
    @ColumnInfo(name = "has_time") val hasTime: Boolean?
)