package com.example.talkeasy.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.Talks
import com.example.talkeasy.data.viewmodel.TalksViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.theme.TalkEasyTheme
import java.time.LocalDateTime

@Composable
fun TalkScreen(talkId: Int, viewModel: TalksViewModel = hiltViewModel()) {
    val navController = LocalNavController.current

    LaunchedEffect(talkId) {
        viewModel.loadTalk(talkId)
    }

    val talk by viewModel.currentTalk.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.navigate("talks") }) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Talk",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
            Text("トーク詳細", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(Modifier.height(16.dp))

        if (talk != null) {
            Text("タイトル: ${talk!!.title}")
            Text("作成日時: ${talk!!.createdAt}")
        } else {
            Text("読み込み中...")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TalkScreenPreview() {
    TalkEasyTheme {
        val sampleTalk = Talks(
            id = 1,
            title = "新しいトーク 1",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("トーク詳細", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("タイトル: ${sampleTalk.title}")
            Text("作成日時: ${sampleTalk.createdAt}")
        }
    }
}