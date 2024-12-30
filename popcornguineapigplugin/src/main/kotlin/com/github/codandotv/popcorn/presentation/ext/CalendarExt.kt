package com.github.codandotv.popcorn.presentation.ext

import java.util.Calendar

internal fun Calendar.dateTimestamp(): String {
    val year = get(Calendar.YEAR)
    val month = get(Calendar.MONTH)
    val dayOfMonth = get(Calendar.DAY_OF_MONTH)
    val hourOfDay = get(Calendar.HOUR_OF_DAY)
    val minute = get(Calendar.MINUTE)
    val second = get(Calendar.SECOND)

    return "$year-$month-$dayOfMonth" + "_" + "$hourOfDay-$minute-$second"
}
