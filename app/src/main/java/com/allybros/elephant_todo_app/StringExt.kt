package com.allybros.elephant_todo_app


/**
 * Created by orcun on 10.07.2022
 */

fun String.addZeroStart(): String {
    if (this.length == 1){
        return "0$this"
    } else {
        return this
    }
}