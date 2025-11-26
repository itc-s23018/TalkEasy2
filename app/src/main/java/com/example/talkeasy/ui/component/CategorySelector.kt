package com.example.talkeasy.ui.component

import androidx.compose.foundation.background
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
    onCategorySelected: (Int?) -> Unit,
    onManageCategories: () -> Unit // ✅ 管理画面遷移用コールバックを追加
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

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
                Text(
                    selectedCategoryId?.let { id ->
                        categories.find { it.id == id }?.name ?: "All"
                    } ?: "All"
                )
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
                        selectedCategoryId = null
                        onCategorySelected(null)
                        expanded = false
                    }
                )
                categories
                    .filter { it.name != "All" }
                    .forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.id
                                onCategorySelected(category.id)
                                expanded = false
                            }
                        )
                    }

                DropdownMenuItem(
                    text = {
                        Text(
                            "カテゴリを管理",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    onClick = {
                        expanded = false
                        onManageCategories()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                )


            }
        }
    }
}

