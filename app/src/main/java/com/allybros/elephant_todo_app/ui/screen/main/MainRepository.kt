package com.allybros.elephant_todo_app.ui.screen.main

import com.allybros.elephant_todo_app.db.Item
import com.allybros.elephant_todo_app.db.ItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Created by orcun on 7.07.2022
 */


class MainRepository @Inject constructor(private val itemDao: ItemDao) {

    suspend fun addItem(item: Item){
        itemDao.insertItem(item)
    }

    val favCoffeeListLiveData = flow {
        val list = itemDao.getNotes()
        emit(list)
    }.flowOn(Dispatchers.IO)
}
