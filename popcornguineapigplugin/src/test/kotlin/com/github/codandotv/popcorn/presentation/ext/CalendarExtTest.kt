package com.github.codandotv.popcorn.presentation.ext

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class CalendarExtTest {

    @Test
    fun `Given a date when dateTimestamp is called then returns a formatted date `() {
        // Arrange
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2012)
            set(Calendar.MONTH, 2)
            set(Calendar.DAY_OF_MONTH, 12)
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 21)
            set(Calendar.SECOND, 32)
        }

        // Act
        val dateTimestamp = calendar.dateTimestamp()

        // Assert
        assertEquals("2012-2-12_1-21-32", dateTimestamp)
    }
}
