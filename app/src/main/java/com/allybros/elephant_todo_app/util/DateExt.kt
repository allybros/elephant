package com.allybros.elephant_todo_app.util

import androidx.compose.ui.text.capitalize
import java.text.DateFormatSymbols
import java.util.*


/**
 * Created by orcun on 20.10.2022
 */

fun Calendar.getYear(): Int = this.get(Calendar.YEAR)

/**
 * It starts zero
 */
fun Calendar.getMonth(): Int = this.get(Calendar.MONTH)

fun Calendar.getDay(): Int = this.get(Calendar.DAY_OF_MONTH)

fun Calendar.getMonthName(): String {
    val monthNames: Array<String> = DateFormatSymbols().months
    return monthNames[this.get(Calendar.MONTH)].capitalized()
}

fun Calendar.getDayName(): String {
    val dayNames: Array<String> = DateFormatSymbols().weekdays
    return dayNames[this.get(Calendar.DAY_OF_WEEK)].capitalized()
}

fun Calendar.nextDay() : Unit = this.add(Calendar.DAY_OF_MONTH, 1)

fun Calendar.previousDay() : Unit = this.add(Calendar.DAY_OF_MONTH, -1)

fun Calendar.getFormattedDate(): String {
    val month = this.getMonth() + 1
    val day = this.getDay()
    val year = this.getYear()
    return "${if (day<10) "0$day" else day}/$month/$year"
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

