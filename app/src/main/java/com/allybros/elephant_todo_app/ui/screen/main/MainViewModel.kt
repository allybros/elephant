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
    @Inject constructor(private val repository: MainRepository) : ViewModel(){
    private val mCalendar: Calendar = GregorianCalendar.getInstance()

    private var _noteListLiveData = MutableStateFlow<List<Item>>(listOf())
    var noteListStateFlow = _noteListLiveData.asStateFlow()

    private var _dialogState = MutableStateFlow(false)
    var dialogStateStateFlow = _dialogState.asStateFlow()

    private val _dayNameLabel = MutableStateFlow(mCalendar.getDayName().plus( ","))
    var dayNameLabelStateFlow = _dayNameLabel.asStateFlow()

    private val _dayAndMonthLabel = MutableStateFlow(
        mCalendar
            .getDay()
            .toString()
            .plus(" ".plus(mCalendar.getMonthName()))
    )
    var dayAndMonthLabelStateFlow = _dayAndMonthLabel.asStateFlow()

    private val _formattedDate = MutableStateFlow(mCalendar.getFormattedDate())
    val formattedDateStateFlow = _formattedDate.asStateFlow()




    fun addItem(item: Item){
        viewModelScope.launch {
            Log.d("TAG", item.toString())
            repository.addItem(item)
        }
    }

    fun getNotes(date: String){
        repository.date = date
        viewModelScope.launch{
            repository.getNotes.collect {
                _noteListLiveData.value = it
            }
        }
    }


    fun onDatePicked(
        pickedDay: Int,
        pickedMonth: Int,
        pickedYear: Int
    ) {
        _formattedDate.value = "${pickedDay.toString().addZeroStart()}/${pickedMonth + 1}/$pickedYear"
        mCalendar.set(Calendar.DAY_OF_MONTH,pickedDay)
        mCalendar.set(Calendar.MONTH,pickedMonth)
        mCalendar.set(Calendar.YEAR,pickedYear)

        getNotes(_formattedDate.value)
        _dayNameLabel.value = mCalendar.getDayName().plus( ",")
        _dayAndMonthLabel.value = mCalendar.getDay().toString().plus(" "+ mCalendar.getMonthName())
    }

    fun onForwardButtonClicked(){
        mCalendar.nextDay()
        onDatePicked(
            mCalendar.getDay(),
            mCalendar.getMonth(),
            mCalendar.getYear()
        )
    }

    fun showAddDialog(it: Boolean){
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