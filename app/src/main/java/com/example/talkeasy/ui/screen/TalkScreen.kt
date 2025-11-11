package com.example.talkeasy.ui.screen

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.InputType
import com.example.talkeasy.data.viewmodel.TalksViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.component.MessageBubble
import com.example.talkeasy.ui.component.MessagesButton
import com.example.talkeasy.ui.component.VoiceMessageBubble
import com.example.talkeasy.ui.dialog.EditTilteDialog
import com.example.talkeasy.ui.dialog.TextInputDialog
import com.example.talkeasy.ui.dialog.VoiceInputDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TalkScreen(talkId: Int, viewModel: TalksViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val talkTitle by viewModel.talkTitle.collectAsState(initial = "新しいトーク")
    val messages by viewModel.messages.collectAsState()
    val tempMessage by viewModel.tempMessage.collectAsState()
    val aiSuggestions by viewModel.aiSuggestions.collectAsState()
    val isGeneratingSuggestions by viewModel.isGeneratingSuggestions.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showVoiceInputDialog by remember { mutableStateOf(false) }
    var showTextInputDialog by remember { mutableStateOf(false) }

    val micPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    LaunchedEffect(Unit) {
        if (!micPermissionState.status.isGranted) {
            micPermissionState.launchPermissionRequest()
        }
    }

    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.JAPANESE
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    LaunchedEffect(talkId) {
        viewModel.loadTalk(talkId)
        viewModel.loadMessages(talkId)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp, start = 12.dp, end = 12.dp)
        ) {
            // ヘッダー
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
                    Text(text = talkTitle, fontSize = 20.sp)
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

            // タイトル編集ダイアログ
            if (showEditDialog) {
                EditTilteDialog(
                    initialTalkTitle = talkTitle,
                    onConfirm = {
                        viewModel.updateTalkTitle(talkId, it)
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }

            // メッセージ表示（吹き出し）
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                messages.forEach { message ->
                    MessageBubble(
                        text = message.text,
                        inputType = message.inputType,
                        onSpeak = { tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, null) }
                    )
                }

                tempMessage?.let {
                    VoiceMessageBubble(
                        text = it.text,
                        isCorrecting = true
                    )
                }
            }

            // 入力ボタン
            MessagesButton(
                onVoiceInputClick = { showVoiceInputDialog = true },
                onKeyboardInputClick = {
                    viewModel.generateReplySuggestions()
                    showTextInputDialog = true
                },
            )

            // 音声入力ダイアログ
            if (showVoiceInputDialog) {
                VoiceInputDialog(
                    onDismiss = { showVoiceInputDialog = false },
                    onResult = { rawText ->
                        viewModel.correctWithFullHistory(talkId, rawText)
                        showVoiceInputDialog = false
                    }
                )
            }

            // テキスト入力ダイアログ（AI候補付き）
            if (showTextInputDialog) {
                TextInputDialog(
                    onDismissRequest = { showTextInputDialog = false },
                    onConfirm = {
                        viewModel.sendMessage(talkId, it, InputType.TEXT)
                        tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
                        showTextInputDialog = false
                    },
                    suggestions = aiSuggestions,
                    isLoading = isGeneratingSuggestions
                )
            }
        }
    }
}

