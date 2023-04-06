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
    var date = ""

    suspend fun addItem(item: Item){
        itemDao.insertItem(item)
    }
    suspend fun deleteItem(item: Item){
        itemDao.deleteItem(item)
    }

    suspend fun updateItem(item: Item){
        itemDao.updateItem(item)
    }
    fun getAllNotes(){
        itemDao.getNotes()
    }

    val getNotesByDate = flow {
        val list = itemDao.getNotesByDates(date)
        emit(list)
    }.flowOn(Dispatchers.IO)

    val getAllNotes = flow {
        val list = itemDao.getNotesByDates(date)
        emit(list)
    }.flowOn(Dispatchers.IO)
}
