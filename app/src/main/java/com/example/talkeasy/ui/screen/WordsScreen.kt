package com.example.talkeasy.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Category
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.ui.component.CategorySelector
import com.example.talkeasy.ui.dialog.InputWordDialog
import com.example.talkeasy.ui.viewmodel.WordsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
    var selectedCategory by remember { mutableStateOf("All") }

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
                onClick = { showDialog = true },
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
            CategorySelector(
                categoryViewModel = categoryViewModel,
                onCategorySelected = { category ->
                    selectedCategory = category
                }
            )

            val words by viewModel.getWords(selectedCategory).collectAsState(initial = emptyList())

            if (words.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_word),
                            contentDescription = "用語未登録",
                            modifier = Modifier.size(250.dp)
                        )
                        Text(
                            text = "用語が保存されていません",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }

            } else {
                words.forEach { word ->
                    WordCard(word)
                }
            }
        }

        if (showDialog) {
            InputWordDialog(
                categoryViewModel = categoryViewModel,
                onConfirm = { word, ruby, category ->
                    viewModel.addWord(word, ruby, category)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

