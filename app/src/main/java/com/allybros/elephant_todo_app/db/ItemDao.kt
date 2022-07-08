package com.allybros.elephant_todo_app.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


/**
 * Created by orcun on 6.07.2022
 */

@Dao
interface ItemDao {

    @Query("SELECT * FROM item where date = :date")
    suspend fun getNotesByDates(date: String): List<Item>

    @Insert
    suspend fun insertItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

}