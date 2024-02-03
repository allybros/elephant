package com.allybros.elephant.util

import java.util.Locale


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
fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

