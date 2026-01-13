package com.example.talkeasy.ui.screen

import androidx.compose.foundation.Image
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
import androidx.navigation.NavController
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.data.viewmodel.WordsViewModel
import com.example.talkeasy.ui.component.CategorySelector
import com.example.talkeasy.ui.component.WordCard
import com.example.talkeasy.ui.dialog.EditWordDialog
import com.example.talkeasy.ui.dialog.InputWordDialog
import com.example.talkeasy.ui.dialog.DeleteWordDialog
import kotlinx.coroutines.launch

// 「マイ辞書」画面。登録した単語の一覧表示、追加、編集、削除を行う
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordsScreen(
    viewModel: WordsViewModel,
    categoryViewModel: CategoryViewModel,
    navController: NavController,
    onBackClick: () -> Unit
) {
    // ダイアログ表示状態、編集/削除対象、選択カテゴリを管理
    var showInputDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<Words?>(null) }
    var wordToDelete by remember { mutableStateOf<Words?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
        // 新規追加ボタン
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // カテゴリ絞り込みセレクター
            CategorySelector(
                categoryViewModel = categoryViewModel,
                onCategorySelected = { categoryId ->
                    selectedCategoryId = categoryId
                },
                onManageCategories = {
                    navController.navigate("category_list")
                }
            )

            // 選択されたカテゴリに基づいて単語リストを取得
            val words by viewModel.getWords(selectedCategoryId).collectAsState(initial = emptyList())

            if (words.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 64.dp),
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
                // 単語リストの表示
                LazyColumn {
                    items(words, key = { it.id }) { word ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) wordToDelete = word
                                false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            modifier = Modifier.height(IntrinsicSize.Min),
                            enableDismissFromStartToEnd = false,
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
                            content = { WordCard(word = word, onClick = { editingWord = word }) }
                        )
                    }
                }
            }
        }

        // 新規追加ダイアログ
        if (showInputDialog) {
            InputWordDialog(
                categoryViewModel = categoryViewModel,
                onConfirm = { word, ruby, categoryId ->
                    viewModel.addWord(word.trim(), ruby.trim(), categoryId)
                    showInputDialog = false
                    coroutineScope.launch { snackbarHostState.showSnackbar("「${word}」が追加されました") }
                },
                onDismiss = { showInputDialog = false }
            )
        }

        // 編集ダイアログ
        editingWord?.let { word ->
            EditWordDialog(
                categoryViewModel = categoryViewModel,
                initialWord = word.word,
                initialRuby = word.wordRuby,
                initialCategoryId = word.categoryId,
                onConfirm = { newWord, newRuby, newCategoryId ->
                    viewModel.updateWord(word.id, newWord.trim(), newRuby.trim(), newCategoryId)
                    editingWord = null
                    coroutineScope.launch { snackbarHostState.showSnackbar("「${newWord}」が更新されました") }
                },
                onDismiss = { editingWord = null }
            )
        }

        // 削除確認ダイアログ
        wordToDelete?.let { word ->
            DeleteWordDialog(
                word = word,
                onConfirm = {
                    viewModel.deleteWord(word)
                    wordToDelete = null
                    coroutineScope.launch { snackbarHostState.showSnackbar("「${word.word}」が削除されました") }
                },
                onDismiss = { wordToDelete = null }
            )
        }
    }
}
