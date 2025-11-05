package com.example.talkeasy.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.viewmodel.TalksViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.theme.TalkEasyTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TalksScreen(viewModel: TalksViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val talks by viewModel.talks.collectAsState()

    TalksScreenContent(
        talks = talks,
        onTalkClick = { talk -> navController.navigate("talk/${talk.id}") },
        onTalkDelete = { talk -> viewModel.deleteTalk(talk) }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalksScreenContent(talks: List<Talks>, onTalkClick: (Talks) -> Unit, onTalkDelete: (Talks) -> Unit) {
    var talkToDelete by remember { mutableStateOf<Talks?>(null) }

    if (talkToDelete != null) {
        AlertDialog(
            onDismissRequest = { talkToDelete = null },
            title = { Text("トークの削除", color = Color.Black) },
            text = { Text("'${talkToDelete?.title}'を本当に削除しますか？", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        talkToDelete?.let(onTalkDelete)
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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(talks, key = { it.id }) { talk ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            talkToDelete = talk
                            return@rememberSwipeToDismissBoxState false
                        }
                        true
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
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTalkClick(talk) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(talk.title, style = MaterialTheme.typography.titleMedium)
                            val formatter = DateTimeFormatter.ofPattern("yy/MM/dd")
                            Text(
                                talk.createdAt.format(formatter),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TalksScreenPreview() {
    TalkEasyTheme {
        val previewTalks = listOf(
            Talks(1, "新しいトーク 1", LocalDateTime.now(), LocalDateTime.now()),
            Talks(2, "新しいトーク 2", LocalDateTime.now(), LocalDateTime.now())
        )
        TalksScreenContent(talks = previewTalks, onTalkClick = {}, onTalkDelete = {})
    }
}