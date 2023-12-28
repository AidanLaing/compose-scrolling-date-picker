package com.aidanlaing.composedobpicker

import org.junit.Assert.assertEquals
import org.junit.Test

class SelectedDateTest {

    @Test
    fun monthAsEnumTypeJan() {
        val selectedDate = SelectedDate(1, 0, 1900)
        val month = selectedDate.monthAsEnumType()
        assertEquals(Month.January, month)
    }

    @Test
    fun monthAsEnumTypeDec() {
        val selectedDate = SelectedDate(1, 11, 1900)
        val month = selectedDate.monthAsEnumType()
        assertEquals(Month.December, month)
    }

    @Test
    fun asTextDecember1st1900() {
        val selectedDate = SelectedDate(1, 11, 1900)
        val text = selectedDate.asText(pattern = "mmmm dz, yyyy")
        assertEquals("December 1st, 1900", text)
    }

    @Test
    fun asText05082000() {
        val selectedDate = SelectedDate(5, 7, 2000)
        val text = selectedDate.asText(pattern = "dd/mm/yyyy")
        assertEquals("05/08/2000", text)
    }

    @Test
    fun asText582000() {
        val selectedDate = SelectedDate(5, 7, 2000)
        val text = selectedDate.asText(pattern = "d/m/yyyy")
        assertEquals("5/8/2000", text)
    }

    @Test
    fun asText10thFeb1996() {
        val selectedDate = SelectedDate(10, 1, 1996)
        val text = selectedDate.asText(pattern = "dz mmm yyyy")
        assertEquals("10th Feb 1996", text)
    }

    @Test
    fun asTextMar22nd1988() {
        val selectedDate = SelectedDate(22, 2, 1988)
        val text = selectedDate.asText(pattern = "mmm dz yyyy")
        assertEquals("Mar 22nd 1988", text)
    }

    @Test
    fun asText1966July03rd() {
        val selectedDate = SelectedDate(3, 6, 1966)
        val text = selectedDate.asText(pattern = "yyyy/mmmm/ddz")
        assertEquals("1966/July/03rd", text)
    }

    @Test
    fun asText1966ReplacedMonthName3rd() {
        val selectedDate = SelectedDate(13, 0, 1966)
        val text = selectedDate.asText(pattern = "yyyy, mmmm, dz", monthNames = listOf("ReplacedMonthName"))
        assertEquals("1966, ReplacedMonthName, 13th", text)
    }
}