package com.github.codandotv.popcorn.presentation.ext

import java.util.Calendar

internal fun Calendar.dateTimestamp(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    return "$year-$month-$dayOfMonth" + "_" + "$hourOfDay-$minute-$second"
}
