package com.allybros.elephant.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allybros.elephant.db.Item
import com.allybros.elephant.util.*
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
    private var _activeDate = MutableStateFlow<Calendar>(GregorianCalendar.getInstance())
    var activeDateStateFlow = _activeDate.asStateFlow()

    private var _taskListLiveData = MutableStateFlow<List<Item>>(listOf())
    var taskListStateFlow = _taskListLiveData.asStateFlow()

    private var _doneTaskListLiveData = MutableStateFlow<List<Item>>(listOf())
    var doneTaskListStateFlow = _doneTaskListLiveData.asStateFlow()

    private var _dialogState = MutableStateFlow(false)
    var dialogStateStateFlow = _dialogState.asStateFlow()

    private val _dayNameLabel = MutableStateFlow(_activeDate.value.getDayName().plus(","))
    var dayNameLabelStateFlow = _dayNameLabel.asStateFlow()

    private val _updatedItem = MutableStateFlow(Item())
    var updatedItemStateFlow = _updatedItem.asStateFlow()


    private val _dayAndMonthLabel = MutableStateFlow(
        _activeDate.value
            .getDay()
            .toString()
            .plus(" ".plus(_activeDate.value.getMonthName()))
    )

    var dayAndMonthLabelStateFlow = _dayAndMonthLabel.asStateFlow()

    init {
        _activeDate.value.set(Calendar.HOUR_OF_DAY, 0)
        _activeDate.value.set(Calendar.MINUTE, 0)
        _activeDate.value.set(Calendar.SECOND, 0)
        _activeDate.value.set(Calendar.MILLISECOND, 0)
        getNotesByDate(_activeDate.value.time.time)
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            repository.addItem(item)
            getNotesByDate(_activeDate.value.time.time)
        }
    }

    fun completeItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
            getNotesByDate(_activeDate.value.time.time)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
            getNotesByDate(_activeDate.value.time.time)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
            getNotesByDate(_activeDate.value.time.time)
        }
    }

    private fun getNotesByDate(date: Long) {
        repository.date = date
        viewModelScope.launch {
            repository.getNotesByDate.collect { item ->
                _taskListLiveData.value = item
                    .sortedBy { it.date?.timeText() }
                    .sortedByDescending { it.hasTime }
                    .sortedBy { it.isComplete }
                _doneTaskListLiveData.value = item.filter { it.isComplete == true }
            }
        }
    }

    fun onDatePicked(date: Long) {
        _activeDate.value.set(date.getYear(), date.getMonth(), date.getDay())

        getNotesByDate(_activeDate.value.time.time)
        _dayNameLabel.value = _activeDate.value.getDayName().plus(",")
        _dayAndMonthLabel.value = _activeDate.value.getDay().toString()
            .plus(" " + _activeDate.value.getMonthName())
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
        _activeDate.value.previousDay()
        onDatePicked(_activeDate.value.time.time)
    }
    fun onForwardButtonClicked() {
        _activeDate.value.nextDay()
        onDatePicked(_activeDate.value.time.time)
    }
}