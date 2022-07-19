package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GroupedCheckbox(mItemsList: Map<String, Boolean>, onClick: (Map<String, Boolean>) -> Unit) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        mItemsList.forEach { items ->
            Column(modifier = Modifier.padding(8.dp)) {
                val isChecked = remember { mutableStateOf(items.value) }

                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = {
                        isChecked.value = it
                        onClick(mItemsList.map { item ->
                            if (item.key == items.key) {
                                item.key to it
                            } else item.toPair()
                        }.toMap())
                    },
                    enabled = true,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Magenta,
                        uncheckedColor = Color.DarkGray,
                        checkmarkColor = Color.Cyan
                    )
                )
                Text(text = items.key)
            }
        }
    }
}
