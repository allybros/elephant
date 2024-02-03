package com.allybros.elephant.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by orcun on 6.07.2022
 */

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "note") var note: String? = "",
    @ColumnInfo(name = "date") var date: Long? = null,
    @ColumnInfo(name = "is_complete") var isComplete: Boolean? = false,
    @ColumnInfo(name = "has_time") var hasTime: Boolean? = false
)