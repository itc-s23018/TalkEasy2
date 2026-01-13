package com.example.talkeasy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.ui.dialog.DeleteCategoryDialog
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.CategoryWithCount
import kotlinx.coroutines.launch

// カテゴリの一覧を表示・管理する画面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categoryViewModel: CategoryViewModel,
    onBackClick: () -> Unit
) {
    // ViewModelから単語数を含むカテゴリリストを取得
    val categoriesWithCount by categoryViewModel.categoriesWithCount.collectAsState()
    // 削除対象のカテゴリを保持する状態変数
    var categoryToDelete by remember { mutableStateOf<CategoryWithCount?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // Snackbarのホスト
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (categoriesWithCount.isEmpty()) {
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
                // カテゴリリストの表示
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // "All"カテゴリを除外して表示
                    items(categoriesWithCount.filter { it.category.name != "All" }, key = { it.category.id }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    categoryToDelete = item
                                }
                                false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            modifier = Modifier.height(IntrinsicSize.Min),
                            enableDismissFromStartToEnd = false, // 右方向のスワイプは無効
                            backgroundContent = {
                                val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Color.Red.copy(alpha = 0.8f) else Color.Transparent
                                Box(
                                    Modifier.fillMaxSize().background(color).padding(end = 16.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.delete),
                                        contentDescription = "Delete",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            // カテゴリカード本体
                            content = {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // カテゴリ名
                                        Text(
                                            text = item.category.name,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        // 登録単語数
                                        Text(
                                            text = "${item.wordCount} 件",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // 削除確認ダイアログの表示
        categoryToDelete?.let { item ->
            // 削除対象カテゴリに属する単語リストを取得
            val words by categoryViewModel.getWordsByCategory(item.category.id).collectAsState(initial = emptyList())

            DeleteCategoryDialog(
                categoryName = item.category.name,
                words = words,
                onConfirm = {
                    // カテゴリを削除
                    categoryViewModel.deleteCategory(item.category)
                    categoryToDelete = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("「${item.category.name}」カテゴリが削除されました")
                    }
                },
                onDismiss = { categoryToDelete = null }
            )
        }
    }
}
