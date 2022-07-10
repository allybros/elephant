package com.allybros.elephant_todo_app.ui.screen.add

import androidx.lifecycle.ViewModel
import com.allybros.elephant_todo_app.db.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * Created by orcun on 7.07.2022
 */

@HiltViewModel
class AddScreenViewModel
    @Inject constructor(private val repository: AddScreenRepository) : ViewModel(){

    suspend fun addItem(item: Item){
        repository.addItem(item)
    }

}