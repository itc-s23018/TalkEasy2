package com.example.talkeasy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.talkeasy.data.entity.Category
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categoryViewModel: CategoryViewModel,
    onBackClick: () -> Unit
) {
    val categories by categoryViewModel.categories.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("カテゴリ一覧") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "戻る",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* 新規追加ダイアログを開く処理をここに */ },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color(0xFF778899)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "カテゴリ追加",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (categories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "カテゴリがまだありません",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    items(categories, key = { it.id }) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row {
                                IconButton(onClick = { /* 編集処理 */ }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit),
                                        contentDescription = "編集"
                                    )
                                }
                                IconButton(onClick = { categoryViewModel.deleteCategory(category) }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.delete),
                                        contentDescription = "削除"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
