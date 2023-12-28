package com.aidanlaing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aidanlaing.composedobpicker.ScrollingSelectionList
import kotlinx.collections.immutable.toPersistentList
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ScrollSelectionListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun scrollSelectionListScrollStartToMidway() {
        ScrollSelectionListRobot(composeTestRule = composeTestRule, numItems = 30, defaultSelectedItem = 0)
            .itemWithTagIsDisplayed("0")
            .scrollToIndex(20)
            .itemWithTagIsDisplayed("20")
            .assertSelectedItems { items -> items == listOf(0, 20) }
    }

    @Test
    fun scrollSelectionListScrollMidwayToEnd() {
        ScrollSelectionListRobot(composeTestRule = composeTestRule, numItems = 31, defaultSelectedItem = 15)
            .itemWithTagIsDisplayed("15")
            .scrollToIndex(31)
            .itemWithTagIsDisplayed("31")
            .assertSelectedItems { items -> items == listOf(15, 31) }
    }

    @Test
    fun scrollSelectionListScrollStartToEndToStart() {
        ScrollSelectionListRobot(composeTestRule = composeTestRule, numItems = 29, defaultSelectedItem = 0)
            .itemWithTagIsDisplayed("0")
            .scrollToIndex(29)
            .itemWithTagIsDisplayed("29")
            .scrollToIndex(0)
            .itemWithTagIsDisplayed("0")
            .assertSelectedItems { items -> items == listOf(0, 29, 0) }
    }

    @Test
    fun scrollSelectionListCorrectNumberOfItemsDisplayed() {
        ScrollSelectionListRobot(
            composeTestRule = composeTestRule,
            numItems = 30,
            defaultSelectedItem = 15,
            numberOfDisplayedItems = 3
        )
            .itemWithTagIsDisplayed("14")
            .itemWithTagIsDisplayed("15")
            .itemWithTagIsDisplayed("16")
            .itemWithTagDoesNotExist("13")
            .itemWithTagDoesNotExist("17")
    }

    private class ScrollSelectionListRobot(
        private val composeTestRule: ComposeContentTestRule,
        private val numItems: Int,
        private val defaultSelectedItem: Int,
        private val selectedItems: MutableList<Int> = mutableListOf(),
        private val numberOfDisplayedItems: Int = 5
    ) {

        init {
            composeTestRule.setContent {
                TestScrollSelectionList()
            }
        }

        fun itemWithTagDoesNotExist(tag: String): ScrollSelectionListRobot {
            composeTestRule.onNodeWithTag(tag).assertDoesNotExist()
            return this@ScrollSelectionListRobot
        }

        fun itemWithTagIsDisplayed(tag: String): ScrollSelectionListRobot {
            composeTestRule.onNodeWithTag(tag).assertIsDisplayed()
            return this@ScrollSelectionListRobot
        }

        fun scrollToIndex(index: Int): ScrollSelectionListRobot {
            composeTestRule.onNodeWithTag(LAZY_COLUMN_TEST_TAG).performScrollToIndex(index)
            return this@ScrollSelectionListRobot
        }

        fun assertSelectedItems(condition: (items: List<Int>) -> Boolean): ScrollSelectionListRobot {
            assertTrue(condition(selectedItems))
            return this@ScrollSelectionListRobot
        }

        @Composable
        @Suppress("TestFunctionName")
        private fun TestScrollSelectionList() {
            ScrollingSelectionList(
                items = (0..numItems).toPersistentList(),
                itemHeightDp = 56.dp,
                defaultSelectedItem = defaultSelectedItem,
                numberOfDisplayedItems = numberOfDisplayedItems,
                getItemText = { item -> item.toString() },
                selectedItemBackground = { heightDp: Dp, paddingTopDp: Dp ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heightDp)
                            .background(color = Color.Black.copy(alpha = 0.1f))
                            .padding(top = paddingTopDp)
                    )
                },
                listItem = { text: String, heightDp: Dp, _ ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heightDp)
                            .background(color = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue).random())
                            .testTag(text)
                    )
                },
                onItemSelected = { item -> selectedItems += item },
                modifier = Modifier.fillMaxWidth(),
                lazyColumnTestTag = LAZY_COLUMN_TEST_TAG
            )
        }
    }

    companion object {
        private const val LAZY_COLUMN_TEST_TAG = "lazy_column_test_tag"
    }
}