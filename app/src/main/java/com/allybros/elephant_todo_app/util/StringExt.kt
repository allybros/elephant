package com.allybros.elephant_todo_app.util


/**
 * Created by orcun on 10.07.2022
 */

fun String.addZeroStart(): String {
    return if (this.length == 1){
        "0$this"
    } else {
        this
    }
}

