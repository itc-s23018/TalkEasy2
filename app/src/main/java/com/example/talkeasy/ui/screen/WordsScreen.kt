package com.example.talkeasy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.ui.component.CategorySelector
import com.example.talkeasy.ui.dialog.InputWordDialog
import com.example.talkeasy.ui.viewmodel.WordsViewModel

@Composable
fun WordCard(word: Words) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = word.word,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = word.wordRubi,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordsScreen(
    viewModel: WordsViewModel,
    categoryViewModel: CategoryViewModel,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("マイ辞書") },
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
                onClick = { showDialog = true },   // ✅ ダイアログ表示
                shape = RoundedCornerShape(16.dp),
                containerColor = Color(0xFF778899)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "用語追加",
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
            // ✅ カテゴリ選択コンポーネント
            CategorySelector(categoryViewModel)

            // ✅ DBから取得したデータを表示
            val words by viewModel.allWords.collectAsState()

            words.forEach { word ->
                WordCard(word)
            }
        }
    }

    // ✅ ダイアログ表示
    if (showDialog) {
        InputWordDialog(
            categoryViewModel = categoryViewModel,
            onConfirm = { word, ruby, category ->
                viewModel.addWord(word, ruby, category)   // ✅ インスタンスのメソッドを呼ぶ
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}
