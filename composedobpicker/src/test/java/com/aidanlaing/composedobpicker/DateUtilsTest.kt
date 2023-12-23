package com.aidanlaing.composedobpicker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun calculateNumDaysForJanuaryFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(0, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 31 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysInFebruaryNonLeapYear() {
        val numDays = calculateNumDaysInMonth(1, 1997)
        assertEquals(28, numDays)
    }

    @Test
    fun calculateNumDaysInFebruaryLeapYear() {
        val numDays = calculateNumDaysInMonth(1, 1996)
        assertEquals(29, numDays)
    }

    @Test
    fun calculateNumDaysInFebruaryLeapYearDivisibleBy400() {
        val numDays = calculateNumDaysInMonth(1, 2000)
        assertEquals(29, numDays)
    }

    @Test
    fun calculateNumDaysInFebruaryNonLeapYearDivisibleBy100() {
        val numDays = calculateNumDaysInMonth(1, 1900)
        assertEquals(28, numDays)
    }

    @Test
    fun calculateNumDaysForMarchFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(2, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 31 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForAprilFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(3, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 30 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForMayFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(4, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 31 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForJuneFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(5, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 30 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForJulyFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(6, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 31 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForAugustFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(7, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 31 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForSeptemberFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(8, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 30 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForOctoberFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(9, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 31 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForNovemberFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(10, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 30 }
        assertTrue(allEqual31)
    }

    @Test
    fun calculateNumDaysForDecemberFor1900To2000() {
        val numDaysList = (1900..2000).map { year -> calculateNumDaysInMonth(11, year) }
        val allEqual31 = numDaysList.all { numDays -> numDays == 31 }
        assertTrue(allEqual31)
    }
}