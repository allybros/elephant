package com.allybros.elephant_todo_app.db

import androidx.room.Database
import androidx.room.RoomDatabase


/**
 * Created by orcun on 6.07.2022
 */

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
