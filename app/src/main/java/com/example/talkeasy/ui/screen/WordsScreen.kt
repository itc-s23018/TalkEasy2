package com.example.talkeasy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.ui.component.CategorySelector
import com.example.talkeasy.ui.theme.TalkEasyTheme
import java.time.LocalDateTime

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
    onBackClick: () -> Unit,
    onAddClick: () -> Unit = {}
) {
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
                onClick = onAddClick,
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

            CategorySelector()

            // 仮データを表示
            val sampleWords = listOf(
                Words(
                    userId = "user1",
                    word = "学校",
                    wordRubi = "がっこう",
                    updatedAt = LocalDateTime.now(),
                    category = "名詞"
                ),
                Words(
                    userId = "user1",
                    word = "走る",
                    wordRubi = "はしる",
                    updatedAt = LocalDateTime.now(),
                    category = "動詞"
                )
            )

            sampleWords.forEach { word ->
                WordCard(word)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WordsScreenPreview() {
    TalkEasyTheme {
        WordsScreen(onBackClick = {}, onAddClick = {})
    }
}
