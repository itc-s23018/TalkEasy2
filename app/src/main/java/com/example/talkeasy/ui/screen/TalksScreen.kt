package com.example.talkeasy.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.viewmodel.TalksListViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.theme.TalkEasyTheme
import java.time.LocalDateTime

@Composable
fun TalksScreen(viewModel: TalksListViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val talks by viewModel.talks.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(talks) { talk ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        navController.navigate("talk/${talk.id}")
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("タイトル: ${talk.title}", style = MaterialTheme.typography.titleMedium)
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
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                listOf(
                    Talks(1, "新しいトーク 1", LocalDateTime.now(), LocalDateTime.now()),
                    Talks(2, "新しいトーク 2", LocalDateTime.now(), LocalDateTime.now())
                )
            ) { talk ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("タイトル: ${talk.title}", style = MaterialTheme.typography.titleMedium)
                        Text("作成日時: ${talk.createdAt}")
                    }
                }
            }
        }
    }
}