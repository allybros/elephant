package com.allybros.elephant_todo_app.ui.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allybros.elephant_todo_app.db.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by orcun on 7.07.2022
 */

@HiltViewModel
class MainViewModel
    @Inject constructor(private val repository: MainRepository) : ViewModel(){

    private var _noteListLiveData = MutableStateFlow<List<Item>>(listOf())
    var noteListStateFlow = _noteListLiveData.asStateFlow()

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

}