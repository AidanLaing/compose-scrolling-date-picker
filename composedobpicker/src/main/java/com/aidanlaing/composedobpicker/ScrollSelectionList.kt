package com.aidanlaing.composedobpicker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun <T> ScrollSelectionList(
    items: ImmutableList<T>,
    itemHeightDp: Dp,
    defaultSelectedItem: T,
    numberOfDisplayedItems: Int,
    getItemText: (T) -> String,
    selectionBackground: @Composable BoxScope.(heightDp: Dp, paddingTopDp: Dp) -> Unit,
    listItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
    onItemSelected: (item: T) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(itemHeightDp * numberOfDisplayedItems)) {
        selectionBackground(
            itemHeightDp,
            itemHeightDp * (numberOfDisplayedItems / 2)
        )

        val scrollState = rememberLazyListState(items.indexOf(defaultSelectedItem))

        var previousSelectedIndex: Int? by remember {
            mutableStateOf(null)
        }

        val selectedIndex by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex
                    .coerceIn(0, items.lastIndex)
                    .also { newSelectedIndex ->
                        if (previousSelectedIndex != newSelectedIndex) {
                            previousSelectedIndex = newSelectedIndex
                            onItemSelected(items[newSelectedIndex])
                        }
                    }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)
        ) {
            items(numberOfDisplayedItems / 2) {
                Box(modifier = Modifier.height(itemHeightDp))
            }

            items(
                items = items,
                key = { item -> getItemText(item) }
            ) { item ->
                val isSelected by remember {
                    derivedStateOf { items[selectedIndex.coerceIn(0, items.lastIndex)] == item }
                }
                listItem(getItemText(item), itemHeightDp, isSelected)
            }

            items(numberOfDisplayedItems / 2) {
                Box(modifier = Modifier.height(itemHeightDp))
            }
        }
    }
}