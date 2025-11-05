package com.example.talkeasy.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.ui.theme.TalkEasyTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TalksScreenContent(
    talks: List<Talks>,
    onTalkClick: (Talks) -> Unit,
    onTalkDelete: (Talks) -> Unit,
    getDaysUntilExpiry: (Talks) -> Long
) {
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
                    confirmValueChange = { false }
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
                            IconButton(
                                onClick = {
                                    talkToDelete = talk
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete),
                                    contentDescription = "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    content = {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTalkClick(talk) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(talk.title, style = MaterialTheme.typography.titleMedium)
                                val formatter = DateTimeFormatter.ofPattern("yy/MM/dd")
                                val daysLeft = getDaysUntilExpiry(talk)
                                val dateColor = if (daysLeft <= 1) Color.Red else Color.Black
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TalksScreenPreview() {
    TalkEasyTheme {
        val now = LocalDateTime.now()
        val previewTalks = listOf(
            Talks(1, "期限切れ間近", now.minusDays(6), now),
            Talks(2, "通常トーク", now.minusDays(2), now)
        )
        TalksScreenContent(
            talks = previewTalks,
            onTalkClick = {},
            onTalkDelete = {},
            getDaysUntilExpiry = { talk ->
                val expiryDate = talk.createdAt.plusWeeks(1)
                val now = LocalDateTime.now()
                Duration.between(now, expiryDate).toDays()
            }
        )
    }
}