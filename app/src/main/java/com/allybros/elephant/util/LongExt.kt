package com.allybros.elephant.util

import java.util.Calendar
import java.util.Date

/**
 * Created by orcun on 2.02.2024
 */

fun Long.getHour(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)
    return calendar.get(Calendar.HOUR_OF_DAY)
}
fun Long.getMinutes(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)
    return calendar.get(Calendar.MINUTE)
}
fun Long.getDay(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)
    return calendar.getDay()
}
fun Long.getMonth(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)
    return calendar.getMonth()
}
fun Long.getYear(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)
    return calendar.getYear()
}
fun Long.timeText(): String {
    return String.format("%02d:%02d",this.getHour() , this.getMinutes())
}
fun Long.addDate(field: Int, amount: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)
    calendar.add(field, amount)
    return calendar.time.time
}