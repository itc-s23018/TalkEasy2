package com.example.talkeasy.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.data.viewmodel.CategoryViewModel

@Composable
fun CategorySelector(
    categoryViewModel: CategoryViewModel,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }

    // ✅ DBからカテゴリ一覧を取得
    val categories by categoryViewModel.categories.collectAsState()

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
                    text = { Text("All") },
                    onClick = {
                        selectedCategory = "All"
                        onCategorySelected("All")
                        expanded = false
                    }
                )

                // ✅ DBに保存されたカテゴリを表示
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category.name
                            onCategorySelected(category.name)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
