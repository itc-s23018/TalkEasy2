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
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.ui.component.CategorySelector
import com.example.talkeasy.ui.component.WordCard
import com.example.talkeasy.ui.dialog.EditWordDialog
import com.example.talkeasy.ui.dialog.InputWordDialog
import com.example.talkeasy.ui.viewmodel.WordsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordsScreen(
    viewModel: WordsViewModel,
    categoryViewModel: CategoryViewModel,
    onBackClick: () -> Unit
) {
    var showInputDialog by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<Words?>(null) }

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
                onClick = { showInputDialog = true },
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
                    WordCard(
                        word = word,
                        onClick = { clickedWord ->
                            editingWord = clickedWord
                            showEditDialog = true
                        }
                    )
                }
            }
        }


        if (showInputDialog) {
            InputWordDialog(
                categoryViewModel = categoryViewModel,
                onConfirm = { word, ruby, category ->
                    viewModel.addWord(word, ruby, category)
                    showInputDialog = false
                },
                onDismiss = { showInputDialog = false }
            )
        }

        editingWord?.let { word ->
            EditWordDialog(
                categoryViewModel = categoryViewModel,
                initialWord = word.word,
                initialRuby = word.wordRubi,
                initialCategory = word.category,
                onConfirm = { newWord, newRuby, newCategory ->
                    viewModel.updateWord(word.id, newWord, newRuby, newCategory)
                    editingWord = null
                },
                onDismiss = { editingWord = null }
            )
        }


    }
}

