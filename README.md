# Compose Scrolling Date Picker
An Android library for selecting a date by scrolling.

[![](https://jitpack.io/v/AidanLaing/compose-scrolling-date-picker.svg)](https://jitpack.io/#AidanLaing/compose-scrolling-date-picker)

## Download

Add the jitpack.io maven url typically in `settings.gradle.kts`
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation "com.github.AidanLaing:compose-scrolling-date-picker:latest-release"
}
```

## Usage
This library exposes two main Composables for selecting a date by scrolling.
1. `[ScrollingDatePicker](scrollingdatepicker/src/main/java/com/aidanlaing/scrollingdatepicker/ScrollingDatePicker.kt)` which is will create 3 scrollable lists for selecting a day, month, and year.
2. `[ScrollingDatePickerDialog](scrollingdatepicker/src/main/java/com/aidanlaing/scrollingdatepicker/ScrollingDatePickerDialog.kt)` which will create a Dialog with a [ScrollingDatePicker](scrollingdatepicker/src/main/java/com/aidanlaing/scrollingdatepicker/ScrollingDatePicker.kt)`

The UI items for each scrolling list can be customized with the `ScrollingDatePickerUi` allowing for the library to match the style of your app.
Default values and localization can be customized with `ScrollingDatePickerProperties`.

`[ScrollingSelectionList](scrollingdatepicker/src/main/java/com/aidanlaing/scrollingdatepicker/ScrollingSelectionList.kt)` can also be used to create your own list which selects the center item when scrolling stops.

### Inline Shared List Item Ui

```kotlin
ScrollingDatePicker(
    scrollingDatePickerUi = ScrollingDatePickerUi.Shared(
        listItem = { text, heightDp, _ ->
            // Your list item Composable
            ScrollingDatePickerItem(text = text, heightDp = heightDp)
        },
        selectedItemBackground = { heightDp, paddingTopDp ->
            Box(
                modifier = Modifier
                    .padding(top = paddingTopDp)
                    .height(heightDp)
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.2f))
            )
        }
    ),
    maxYear = Calendar.getInstance().get(Calendar.YEAR),
    dateChanged = dateChanged,
    modifier = Modifier.fillMaxWidth()
)
```

### Dialog Separated List Item Ui

```kotlin
ScrollingDatePickerDialog(
    scrollingDatePickerUi = ScrollingDatePickerUi.Separated(
        dayListItem = { text, heightDp, _ ->
            ScrollingDatePickerItem(text = text, heightDp = heightDp) // Your list item Composable
        },
        monthListItem = { text, heightDp, _ ->
            ScrollingDatePickerItem(text = text, heightDp = heightDp) // Your list item Composable
        },
        yearListItem = { text, heightDp, _ ->
            ScrollingDatePickerItem(text = text, heightDp = heightDp) // Your list item Composable
        }
    ),
    maxYear = Calendar.getInstance().get(Calendar.YEAR),
    backgroundColor = MaterialTheme.colorScheme.surface,
    dateChanged = dateConfirmed,
    onDismissRequest = { showScrollingDatePickerDialog = false },
    scrollingDatePickerProperties = ScrollingDatePickerProperties(
        defaultSelectedDay = selectedDate?.day ?: 1,
        defaultSelectedMonth = selectedDate?.month ?: 0,
        defaultSelectedYear = selectedDate?.year ?: 2000
    ),
    footerContent = {
        DialogFooterContent(
            onDismiss = { showScrollingDatePickerDialog = false },
            onConfirm = {
                showScrollingDatePickerDialog = false
                selectedDate?.let { newDate -> dateConfirmed(newDate) }
            }
        )
    }
)
```