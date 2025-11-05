package com.example.talkeasy.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.talkeasy.R
import com.example.talkeasy.data.viewmodel.MessagesViewModel
import com.example.talkeasy.data.viewmodel.TalksViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.dialog.EditTilteDialog
import com.example.talkeasy.ui.theme.TalkEasyTheme

@Composable
fun TalkScreen(
    talkId: Int,
    talksViewModel: TalksViewModel = hiltViewModel(),
    messagesViewModel: MessagesViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val talkTitle by talksViewModel.talkTitle.collectAsState(initial = "新しいトーク")
    val messages by messagesViewModel.messages.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(talkId) {
        talksViewModel.loadTalk(talkId)
        messagesViewModel.loadMessages(talkId)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp, start = 12.dp, end = 12.dp)
        ) {
            // タイトルバー
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

            // メッセージ表示（吹き出し形式）
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageBubble(text = message.text)
                }
            }

            // メッセージ送信欄
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("メッセージを入力") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                messagesViewModel.sendMessage(talkId, inputText)
                                inputText = ""
                                focusManager.clearFocus()
                            }
                        }
                    )
                )
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            messagesViewModel.sendMessage(talkId, inputText)
                            inputText = ""
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("送信")
                }
            }

            if (showEditDialog) {
                EditTilteDialog(
                    initialTalkTitle = talkTitle,
                    onConfirm = { newTitle ->
                        talksViewModel.updateTalkTitle(talkId, newTitle)
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = text,
            modifier = Modifier
                .background(Color(0xFFDCF8C6), RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(max = 280.dp),
            fontSize = 16.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TalkScreenPreview() {
    TalkEasyTheme {
        TalkScreen(talkId = 0)
    }
}
