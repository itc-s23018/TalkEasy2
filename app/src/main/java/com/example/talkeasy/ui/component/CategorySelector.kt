package com.example.talkeasy.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R

@Composable
fun CategorySelector() {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("挨拶") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "カテゴリ：",
            style = MaterialTheme.typography.titleMedium
        )

        // 右側にドロップダウン
        Box {
            TextButton(onClick = { expanded = true }) {
                Text(selectedCategory)
                Icon(
                    painter = painterResource(id = R.drawable.drop),
                    contentDescription = "カテゴリ選択",
                    tint = Color.Black,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("挨拶") },
                    onClick = {
                        selectedCategory = "挨拶"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("名詞") },
                    onClick = {
                        selectedCategory = "名詞"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("動詞") },
                    onClick = {
                        selectedCategory = "動詞"
                        expanded = false
                    }
                )
            }
        }
    }
}
