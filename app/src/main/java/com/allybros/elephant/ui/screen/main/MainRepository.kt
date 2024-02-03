package com.allybros.elephant.ui.screen.main

import com.allybros.elephant.db.Item
import com.allybros.elephant.db.ItemDao
import com.allybros.elephant.util.addDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Calendar
import javax.inject.Inject

/**
 * Created by orcun on 7.07.2022
 */


class MainRepository @Inject constructor(private val itemDao: ItemDao) {
    var date: Long = 0

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
        val list = itemDao.getNotesByDates(date, date.addDate(Calendar.DAY_OF_MONTH, 1))
        emit(list)
    }.flowOn(Dispatchers.IO)

    val getAllNotes = flow {
        val list = itemDao.getNotes()
        emit(list)
    }.flowOn(Dispatchers.IO)
}
