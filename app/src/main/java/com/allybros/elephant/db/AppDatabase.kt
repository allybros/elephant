package com.allybros.elephant.db

import androidx.room.Database
import androidx.room.RoomDatabase


/**
 * Created by orcun on 6.07.2022
 */

@Database(
    version = 3,
    entities = [Item::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
