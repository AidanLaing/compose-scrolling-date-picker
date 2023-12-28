package com.aidanlaing.scrollingdatepicker

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
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ScrollingDatePickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun scrollingDatePickerInitialState() {
        ScrollingDatePickerRobot(composeTestRule = composeTestRule)
            .itemWithTagIsDisplayed("2000")
            .itemWithTagIsDisplayed("January")
            .itemWithTagIsDisplayed("1")
            .assertSelectedItems { items -> items.last() == SelectedDate(1, 0, 2000) }
    }

    @Test
    fun scrollingDatePickerLeapYearFeb29thSelection() {
        ScrollingDatePickerRobot(composeTestRule = composeTestRule)
            .scrollToIndex(96, "year_lazy_column_test_tag")
            .scrollToIndex(1, "month_lazy_column_test_tag")
            .scrollToIndex(28, "day_lazy_column_test_tag")
            .itemWithTagIsDisplayed("1996")
            .itemWithTagIsDisplayed("February")
            .itemWithTagIsDisplayed("29")
            .assertSelectedItems { items -> items.last() == SelectedDate(29, 1, 1996) }
    }

    @Test
    fun scrollingDatePickerMarch31ToFeb28NotLeapYear() {
        ScrollingDatePickerRobot(composeTestRule = composeTestRule)
            .scrollToIndex(95, "year_lazy_column_test_tag")
            .scrollToIndex(2, "month_lazy_column_test_tag")
            .scrollToIndex(30, "day_lazy_column_test_tag")
            .scrollToIndex(1, "month_lazy_column_test_tag")
            .itemWithTagIsDisplayed("1995")
            .itemWithTagIsDisplayed("February")
            .itemWithTagIsDisplayed("28")
            .assertSelectedItems { items -> items.last() == SelectedDate(28, 1, 1995) }
    }

    private class ScrollingDatePickerRobot(
        private val composeTestRule: ComposeContentTestRule,
        private val selectedDateItems: MutableList<SelectedDate> = mutableListOf()
    ) {

        init {
            composeTestRule.setContent {
                TestScrollingDatePicker()
            }
        }

        fun itemWithTagIsDisplayed(tag: String): ScrollingDatePickerRobot {
            composeTestRule.onNodeWithTag(tag).assertIsDisplayed()
            return this@ScrollingDatePickerRobot
        }

        fun scrollToIndex(index: Int, tag: String): ScrollingDatePickerRobot {
            composeTestRule.onNodeWithTag(tag).performScrollToIndex(index)
            return this@ScrollingDatePickerRobot
        }

        fun assertSelectedItems(condition: (items: List<SelectedDate>) -> Boolean): ScrollingDatePickerRobot {
            Assert.assertTrue(condition(selectedDateItems))
            return this@ScrollingDatePickerRobot
        }

        @Composable
        @Suppress("TestFunctionName")
        private fun TestScrollingDatePicker() {
            ScrollingDatePicker(
                scrollingDatePickerUi = ScrollingDatePickerUi.Unified(
                    listItem = { text, heightDp, _ ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(heightDp)
                                .background(color = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue).random())
                                .testTag(text)
                        )
                    }
                ),
                maxYear = 2023,
                dateChanged = { newDate ->
                    selectedDateItems += newDate
                }
            )
        }
    }
}