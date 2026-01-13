package com.example.talkeasy.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.viewmodel.TalksViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

// 過去のトーク一覧を表示する画面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalksScreen(
    viewModel: TalksViewModel = hiltViewModel(),
    onTalkClick: (Talks) -> Unit = {} // トークがクリックされたときの処理
) {
    val talks by viewModel.talks.collectAsState()
    // 削除対象のトークを保持する状態変数
    var talkToDelete by remember { mutableStateOf<Talks?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 削除確認ダイアログ
    if (talkToDelete != null) {
        AlertDialog(
            onDismissRequest = { talkToDelete = null },
            title = { Text("トークの削除", color = Color.Black) },
            text = { Text("'${talkToDelete?.title}'を本当に削除しますか？", color = Color.Black) },
            // 削除ボタン
            confirmButton = {
                Button(
                    onClick = {
                        talkToDelete?.let {
                            viewModel.deleteTalk(it)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("「${it.title}」が消されました")
                            }
                        }
                        talkToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                Button(
                    onClick = { talkToDelete = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("キャンセル")
                }
            },
            containerColor = Color.White,
            tonalElevation = 4.dp
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("トーク一覧画面") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // Snackbarのホスト
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(talks, key = { it.id }) { talk ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            talkToDelete = talk
                        }
                        false // スワイプで要素が自動的に消えないようにする
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        val color = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                            else -> Color.Transparent
                        }
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(end = 16.dp),
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
                    // 通常表示されるコンテンツ（トークカード）
                    content = {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTalkClick(talk) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(talk.title, style = MaterialTheme.typography.titleMedium)
                                val formatter = DateTimeFormatter.ofPattern("yy/MM/dd")
                                // 保存期間は1週間
                                val expiryDate = talk.createdAt.plusWeeks(1)
                                val daysLeft = Duration.between(LocalDateTime.now(), expiryDate).toDays()
                                // 残り3日以下で日付の色を赤に
                                val dateColor = if (daysLeft <= 3) Color.Red else Color.Black

                                Text(
                                    talk.createdAt.format(formatter),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    color = dateColor
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
