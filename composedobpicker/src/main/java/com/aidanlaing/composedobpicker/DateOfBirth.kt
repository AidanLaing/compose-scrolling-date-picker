package com.aidanlaing.composedobpicker

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale

data class DateOfBirth(val day: Int, val month: Int, val year: Int) {

    fun monthAsEnumType(): Month = Month.values()[month]

    /**
     * Supported patterns
     * Day:     dd      -   numeric day padded length
     *          d       -   numeric day not padded
     *          z       -   day postfix (st, nd, rd, th)
     * Month:   mmmm    -   text month full length
     *          mmm     -   text month shortened length
     *          mm      -   numeric month padded length
     *          m       -   numeric month not padded
     * Year:    yyyy    -   numeric year full length
     */
    fun asText(
        pattern: String = "mmmm dz, yyyy",
        monthNames: List<String> = Month.values().map { month -> month.name }.toList(),
        padChar: Char = '0',
        padLength: Int = 2,
        shortenedMonthLength: Int = 3,
        locale: Locale = Locale.current
    ): String = pattern
        .replace("yyyy", year.toString())
        .replace("dd", day.toString().padStart(length = padLength, padChar = padChar))
        .replace("d", day.toString())
        .replace("z", getDayPostFix())
        .replace("mmmm", monthNames[month].uppercase())
        .replace("mmm", monthNames[month].take(shortenedMonthLength).uppercase())
        .replace("mm", (month + 1).toString().padStart(length = padLength, padChar = padChar))
        .replace("m", (month + 1).toString())
        .lowercase()
        .capitalize(locale = locale)

    private fun getDayPostFix(): String {
        val dayString = day.toString()
        return when {
            dayString.endsWith("1") && day != 11 -> "st"
            dayString.endsWith("2") && day != 12 -> "nd"
            dayString.endsWith("3") && day != 13 -> "rd"
            else -> "th"
        }
    }
}