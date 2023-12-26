package com.aidanlaing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import com.aidanlaing.composedobpicker.DateOfBirth
import com.aidanlaing.composedobpicker.DateOfBirthPicker
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class DateOfBirthPickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dateOfBirthPickerInitialState() {
        DateOfBirthRobot(composeTestRule = composeTestRule)
            .itemWithTagIsDisplayed("2000")
            .itemWithTagIsDisplayed("January")
            .itemWithTagIsDisplayed("1")
            .assertSelectedItems { items -> items.last() == DateOfBirth(1, 0, 2000) }
    }

    @Test
    fun dateOfBirthPickerLeapYearFeb29thSelection() {
        DateOfBirthRobot(composeTestRule = composeTestRule)
            .scrollToIndex(96, "year_lazy_column_test_tag")
            .scrollToIndex(1, "month_lazy_column_test_tag")
            .scrollToIndex(28, "day_lazy_column_test_tag")
            .itemWithTagIsDisplayed("1996")
            .itemWithTagIsDisplayed("February")
            .itemWithTagIsDisplayed("29")
            .assertSelectedItems { items -> items.last() == DateOfBirth(29, 1, 1996) }
    }

    @Test
    fun dateOfBirthPickerMarch31ToFeb28NotLeapYear() {
        DateOfBirthRobot(composeTestRule = composeTestRule)
            .scrollToIndex(95, "year_lazy_column_test_tag")
            .scrollToIndex(2, "month_lazy_column_test_tag")
            .scrollToIndex(30, "day_lazy_column_test_tag")
            .scrollToIndex(1, "month_lazy_column_test_tag")
            .itemWithTagIsDisplayed("1995")
            .itemWithTagIsDisplayed("February")
            .itemWithTagIsDisplayed("28")
            .assertSelectedItems { items -> items.last() == DateOfBirth(28, 1, 1995) }
    }

    private class DateOfBirthRobot(
        private val composeTestRule: ComposeContentTestRule,
        private val selectedDateOfBirthItems: MutableList<DateOfBirth> = mutableListOf()
    ) {

        init {
            composeTestRule.setContent {
                TestDateOfBirthPicker()
            }
        }

        fun itemWithTagIsDisplayed(tag: String): DateOfBirthRobot {
            composeTestRule.onNodeWithTag(tag).assertIsDisplayed()
            return this@DateOfBirthRobot
        }

        fun scrollToIndex(index: Int, tag: String): DateOfBirthRobot {
            composeTestRule.onNodeWithTag(tag).performScrollToIndex(index)
            return this@DateOfBirthRobot
        }

        fun assertSelectedItems(condition: (items: List<DateOfBirth>) -> Boolean): DateOfBirthRobot {
            Assert.assertTrue(condition(selectedDateOfBirthItems))
            return this@DateOfBirthRobot
        }

        @Composable
        @Suppress("TestFunctionName")
        private fun TestDateOfBirthPicker() {
            DateOfBirthPicker(
                defaultListItem = { text, heightDp, _ ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heightDp)
                            .background(color = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue).random())
                            .testTag(text)
                    )
                },
                dateOfBirthChanged = { dateOfBirth ->
                    selectedDateOfBirthItems += dateOfBirth
                },
                maxYear = 2023
            )
        }
    }
}