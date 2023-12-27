package com.aidanlaing.composedobpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

sealed class DateOfBirthPickerUi {

    @Immutable
    data class Unified(
        val listItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
        val selectedItemBackground: @Composable BoxScope.(
            heightDp: Dp,
            paddingTopDp: Dp
        ) -> Unit = { heightDp, paddingTopDp -> DefaultSelectedItemBackground(heightDp, paddingTopDp) }
    ) : DateOfBirthPickerUi()

    @Immutable
    data class Separate(
        val dayListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
        val monthListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
        val yearListItem: @Composable LazyItemScope.(text: String, heightDp: Dp, isSelected: Boolean) -> Unit,
        val daySelectedItemBackground: @Composable BoxScope.(
            heightDp: Dp,
            paddingTopDp: Dp
        ) -> Unit = { heightDp, paddingTopDp -> DefaultSelectedItemBackground(heightDp, paddingTopDp) },
        val monthSelectedItemBackground: @Composable BoxScope.(
            heightDp: Dp,
            paddingTopDp: Dp
        ) -> Unit = { heightDp, paddingTopDp -> DefaultSelectedItemBackground(heightDp, paddingTopDp) },
        val yearSelectedItemBackground: @Composable BoxScope.(
            heightDp: Dp,
            paddingTopDp: Dp
        ) -> Unit = { heightDp, paddingTopDp -> DefaultSelectedItemBackground(heightDp, paddingTopDp) }
    ) : DateOfBirthPickerUi()

    fun determineDayListItem() = when (this) {
        is Unified -> listItem
        is Separate -> dayListItem
    }

    fun determineMonthListItem() = when (this) {
        is Unified -> listItem
        is Separate -> monthListItem
    }

    fun determineYearListItem() = when (this) {
        is Unified -> listItem
        is Separate -> yearListItem
    }

    fun determineDaySelectedItemBackground() = when (this) {
        is Unified -> selectedItemBackground
        is Separate -> daySelectedItemBackground
    }

    fun determineMonthSelectedItemBackground() = when (this) {
        is Unified -> selectedItemBackground
        is Separate -> monthSelectedItemBackground
    }

    fun determineYearSelectedItemBackground() = when (this) {
        is Unified -> selectedItemBackground
        is Separate -> yearSelectedItemBackground
    }
}

@Composable
private fun DefaultSelectedItemBackground(heightDp: Dp, paddingTopDp: Dp) {
    Box(
        modifier = Modifier
            .padding(top = paddingTopDp)
            .height(heightDp)
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.2f))
    )
}