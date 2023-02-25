package com.allybros.elephant_todo_app.ui.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allybros.elephant_todo_app.db.Item
import com.allybros.elephant_todo_app.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


/**
 * Created by orcun on 7.07.2022
 */

@HiltViewModel
class MainViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {
    private val mCalendar: Calendar = GregorianCalendar.getInstance()

    private var _taskListLiveData = MutableStateFlow<List<Item>>(listOf())
    var taskListStateFlow = _taskListLiveData.asStateFlow()

    private var _doneTaskListLiveData = MutableStateFlow<List<Item>>(listOf())
    var doneTaskListStateFlow = _doneTaskListLiveData.asStateFlow()

    private var _dialogState = MutableStateFlow(false)
    var dialogStateStateFlow = _dialogState.asStateFlow()

    private val _dayNameLabel = MutableStateFlow(mCalendar.getDayName().plus(","))
    var dayNameLabelStateFlow = _dayNameLabel.asStateFlow()

    private val _updatedItem = MutableStateFlow(Item())
    var updatedItemStateFlow = _updatedItem.asStateFlow()


    private val _dayAndMonthLabel = MutableStateFlow(
        mCalendar
            .getDay()
            .toString()
            .plus(" ".plus(mCalendar.getMonthName()))
    )
    var dayAndMonthLabelStateFlow = _dayAndMonthLabel.asStateFlow()

    private val _formattedDate = MutableStateFlow(mCalendar.getFormattedDate())
    val formattedDateStateFlow = _formattedDate.asStateFlow()


    fun addItem(item: Item) {
        viewModelScope.launch {
            Log.d("ADDED", item.toString())
            repository.addItem(item)
        }
    }

    fun completeItem(item: Item) {
        viewModelScope.launch {
            Log.d("Completed", item.toString())
            repository.updateItem(item)
            getNotes(_formattedDate.value)
        }
    }


    fun deleteItem(item: Item) {
        viewModelScope.launch {
            Log.d("DELETED", item.toString())
            repository.deleteItem(item)
            getNotes(_formattedDate.value)
        }
    }


    fun updateItem(item: Item) {
        viewModelScope.launch {
            Log.d("UPDATED", item.toString())
            repository.updateItem(item)
        }
    }

    fun getNotes(date: String) {
        repository.date = date
        viewModelScope.launch {
            repository.getNotes.collect { item ->
                _taskListLiveData.value = item
                    .sortedBy { it.time }
                    .sortedByDescending { it.hasTime }
                    .sortedBy { it.isComplete }
                _doneTaskListLiveData.value = item.filter { it.isComplete == true }
                item.forEach { Log.d("ITEMS: ", it.toString()) }
            }
        }
    }


    fun onDatePicked(
        pickedDay: Int,
        pickedMonth: Int,
        pickedYear: Int
    ) {
        _formattedDate.value =
            "${pickedDay.toString().addZeroStart()}/${pickedMonth + 1}/$pickedYear"
        mCalendar.set(Calendar.DAY_OF_MONTH, pickedDay)
        mCalendar.set(Calendar.MONTH, pickedMonth)
        mCalendar.set(Calendar.YEAR, pickedYear)

        getNotes(_formattedDate.value)
        _dayNameLabel.value = mCalendar.getDayName().plus(",")
        _dayAndMonthLabel.value = mCalendar.getDay().toString().plus(" " + mCalendar.getMonthName())
    }

    fun onForwardButtonClicked() {
        mCalendar.nextDay()
        onDatePicked(
            mCalendar.getDay(),
            mCalendar.getMonth(),
            mCalendar.getYear()
        )
    }

    fun setUpdatedItem(item: Item) {
        _updatedItem.value = item
    }

    fun showAddDialog(
        it: Boolean
    ) {
        _dialogState.value = it
    }

    fun onBackButtonClicked() {
        mCalendar.previousDay()
        onDatePicked(
            mCalendar.getDay(),
            mCalendar.getMonth(),
            mCalendar.getYear()
        )
    }
}