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
    fun getNotesByDates(date: String): List<Item>

    @Insert
    fun insertItem(item: Item)

    @Delete
    fun deleteItem(item: Item)

}