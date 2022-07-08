package com.allybros.elephant_todo_app.ui.screen.add

import com.allybros.elephant_todo_app.db.Item
import com.allybros.elephant_todo_app.db.ItemDao
import javax.inject.Inject


/**
 * Created by orcun on 7.07.2022
 */


class AddScreenRepository @Inject constructor(private val itemDao: ItemDao){

    suspend fun addItem(item: Item){
        itemDao.insertItem(item)
    }
}