package com.aidanlaing.composedobpicker

import org.junit.Assert.assertEquals
import org.junit.Test

class DateOfBirthTest {

    @Test
    fun monthAsEnumTypeJan() {
        val dateOfBirth = DateOfBirth(1, 0, 1900)
        val month = dateOfBirth.monthAsEnumType()
        assertEquals(Month.January, month)
    }

    @Test
    fun monthAsEnumTypeDec() {
        val dateOfBirth = DateOfBirth(1, 11, 1900)
        val month = dateOfBirth.monthAsEnumType()
        assertEquals(Month.December, month)
    }

    @Test
    fun asTextDecember1st1900() {
        val dateOfBirth = DateOfBirth(1, 11, 1900)
        val text = dateOfBirth.asText(pattern = "mmmm dz, yyyy")
        assertEquals("December 1st, 1900", text)
    }

    @Test
    fun asText05082000() {
        val dateOfBirth = DateOfBirth(5, 7, 2000)
        val text = dateOfBirth.asText(pattern = "dd/mm/yyyy")
        assertEquals("05/08/2000", text)
    }

    @Test
    fun asText582000() {
        val dateOfBirth = DateOfBirth(5, 7, 2000)
        val text = dateOfBirth.asText(pattern = "d/m/yyyy")
        assertEquals("5/8/2000", text)
    }

    @Test
    fun asText10thFeb1996() {
        val dateOfBirth = DateOfBirth(10, 1, 1996)
        val text = dateOfBirth.asText(pattern = "dz mmm yyyy")
        assertEquals("10th Feb 1996", text)
    }

    @Test
    fun asTextMar22nd1988() {
        val dateOfBirth = DateOfBirth(22, 2, 1988)
        val text = dateOfBirth.asText(pattern = "mmm dz yyyy")
        assertEquals("Mar 22nd 1988", text)
    }

    @Test
    fun asText1966July03rd() {
        val dateOfBirth = DateOfBirth(3, 6, 1966)
        val text = dateOfBirth.asText(pattern = "yyyy/mmmm/ddz")
        assertEquals("1966/July/03rd", text)
    }

    @Test
    fun asText1966ReplacedMonthName3rd() {
        val dateOfBirth = DateOfBirth(13, 0, 1966)
        val text = dateOfBirth.asText(pattern = "yyyy, mmmm, dz", monthNames = listOf("ReplacedMonthName"))
        assertEquals("1966, ReplacedMonthName, 13th", text)
    }
}