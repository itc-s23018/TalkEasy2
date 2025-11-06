package com.example.talkeasy.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.talkeasy.R
import com.example.talkeasy.data.viewmodel.TalksViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.component.MessagesButton
import com.example.talkeasy.ui.dialog.EditTilteDialog
import com.example.talkeasy.ui.dialog.TextInputDialog
import com.example.talkeasy.ui.dialog.VoiceInputDialog
import com.example.talkeasy.ui.theme.TalkEasyTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TalkScreen(talkId: Int, viewModel: TalksViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val talkTitle by viewModel.talkTitle.collectAsState(initial = "新しいトーク")
    var showEditDialog by remember { mutableStateOf(false) }
    var showVoiceInputDialog by remember { mutableStateOf(false) }
    var showTextInputDialog by remember { mutableStateOf(false) }



    LaunchedEffect(talkId) {
        viewModel.loadTalk(talkId)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp, start = 12.dp, end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate("tabs/1") },
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Talk",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Black
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = talkTitle,
                        fontSize = 20.sp,
                    )
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "Edit",
                            modifier = Modifier.size(35.dp),
                            tint = Color.Black
                        )
                    }
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Check",
                        modifier = Modifier.size(35.dp),
                        tint = Color.Black
                    )
                }
            }

            if (showEditDialog) {
                EditTilteDialog(
                    initialTalkTitle = talkTitle,
                    onConfirm = { newTitle ->
                        viewModel.updateTalkTitle(talkId, newTitle)
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            MessagesButton(
                onVoiceInputClick = { showVoiceInputDialog = true },
                onKeyboardInputClick = { showTextInputDialog = true },
            )

            if (showVoiceInputDialog) {
                VoiceInputDialog(
                    onDismiss = { showVoiceInputDialog = false },
                    onResult = { result ->
                        // 音声認識結果の処理（例：タイトル更新やメッセージ追加など）
                        println("音声認識結果: $result")
                    }
                )
            }

            if (showTextInputDialog) {
                TextInputDialog(
                    onDismissRequest = { showTextInputDialog = false },
                    onConfirm = { result ->
                        println("入力されたテキスト: $result")
                    }
                )
            }
    }
}
}
