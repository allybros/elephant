package com.allybros.elephant_todo_app.util

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
    return monthNames[this.get(Calendar.MONTH)]
}

fun Calendar.getDayName(): String {
    val dayNames: Array<String> = DateFormatSymbols().weekdays
    return dayNames[this.get(Calendar.DAY_OF_WEEK)]
}

