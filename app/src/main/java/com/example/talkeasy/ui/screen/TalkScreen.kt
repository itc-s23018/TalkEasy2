package com.example.talkeasy.ui.screen

import android.speech.tts.TextToSpeech
import android.util.Log
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
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.data.viewmodel.TalksViewModel
import com.example.talkeasy.data.viewmodel.TopViewModel
import com.example.talkeasy.gemini.GeminiWord
import com.example.talkeasy.ui.viewmodel.WordsViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.component.MessageBubble
import com.example.talkeasy.ui.component.MessagesButton
import com.example.talkeasy.ui.component.VoiceMessageBubble
import com.example.talkeasy.ui.dialog.DictionaryDialog
import com.example.talkeasy.ui.dialog.EditTilteDialog
import com.example.talkeasy.ui.dialog.TextInputDialog
import com.example.talkeasy.ui.dialog.VoiceInputDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TalkScreen(
    talkId: Int,
    talksViewModel: TalksViewModel = hiltViewModel(),
    wordsViewModel: WordsViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    topViewModel: TopViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val talkTitle by talksViewModel.talkTitle.collectAsState(initial = "新しいトーク")
    val messages by talksViewModel.messages.collectAsState()
    val tempMessage by talksViewModel.tempMessage.collectAsState()
    val aiSuggestions by talksViewModel.aiSuggestions.collectAsState()
    val isGeneratingSuggestions by talksViewModel.isGeneratingSuggestions.collectAsState()

    val allWords by wordsViewModel.allWords.collectAsState()
    val extractedWordsMap by wordsViewModel.extractedWordsMap.collectAsState()
    val currentExtractedWords = extractedWordsMap[talkId] ?: emptyList()

    val user = topViewModel.user

    var showEditDialog by remember { mutableStateOf(false) }
    var showVoiceInputDialog by remember { mutableStateOf(false) }
    var showTextInputDialog by remember { mutableStateOf(false) }
    var showDictionaryDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
        talksViewModel.loadTalk(talkId)
        talksViewModel.loadMessages(talkId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
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

                // 👇 ログイン済みなら AI アシストボタンを表示
                if (topViewModel.isLoggedIn) {
                    IconButton(
                        onClick = {
                            GeminiWord.extractTermsFromHistory(
                                history = messages.map { it.text },
                                onResult = { terms ->
                                    wordsViewModel.addExtractedWords(talkId, terms, allWords)
                                    showDictionaryDialog = true
                                },
                                onError = { error ->
                                    Log.e("TalkScreen", "Gemini抽出失敗: $error")
                                }
                            )
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.spellcheck),
                            contentDescription = "Check",
                            modifier = Modifier.size(35.dp),
                            tint = if (currentExtractedWords.isNotEmpty()) Color.Red else Color.Black
                        )
                    }
                }
            }

            if (showEditDialog) {
                EditTilteDialog(
                    initialTalkTitle = talkTitle,
                    onConfirm = {
                        talksViewModel.updateTalkTitle(talkId, it)
                        showEditDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("「${it}」が更新されました")
                        }
                    },
                    onDismiss = { showEditDialog = false }
                )
            }

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
                    VoiceMessageBubble(text = it.text, isCorrecting = true)
                }
            }

            if (showDictionaryDialog) {
                DictionaryDialog(
                    onDismiss = { showDictionaryDialog = false },
                    words = currentExtractedWords,
                    categoryViewModel = categoryViewModel,
                    wordsViewModel = wordsViewModel,
                    talkId = talkId,
                    allWords = allWords,
                    messages = messages.map { it.text },
                    onWordSaved = { word ->
                        wordsViewModel.addWord(word.word, word.wordRuby, word.categoryId)
                        wordsViewModel.removeExtractedWord(talkId, word)
                    }
                )
            }

            MessagesButton(
                onVoiceInputClick = { showVoiceInputDialog = true },
                onKeyboardInputClick = {
                    talksViewModel.generateReplySuggestions(
                        allWords = allWords,
                        categories = categoryViewModel.categories.value
                    )
                    showTextInputDialog = true
                },
            )

            if (showVoiceInputDialog) {
                VoiceInputDialog(
                    onDismiss = { showVoiceInputDialog = false },
                    onResult = { rawText ->
                        if (topViewModel.isLoggedIn) {
                            talksViewModel.correctWithFullHistory(
                                talkId = talkId,
                                rawText = rawText,
                                dbWords = allWords,
                                user = user
                            )
                            GeminiWord.extractTermsFromHistory(
                                history = messages.map { it.text } + rawText,
                                onResult = { terms ->
                                    wordsViewModel.addExtractedWords(talkId, terms, allWords)
                                },
                                onError = { error -> Log.e("TalkScreen", "Gemini抽出失敗: $error") }
                            )
                        } else {
                            talksViewModel.sendMessage(talkId, rawText, InputType.VOICE)
                        }
                        showVoiceInputDialog = false
                    }
                )
            }

            if (showTextInputDialog) {
                TextInputDialog(
                    onDismissRequest = { showTextInputDialog = false },
                    onConfirm = { inputText ->
                        talksViewModel.sendMessage(talkId, inputText, InputType.TEXT)
                        tts?.speak(inputText, TextToSpeech.QUEUE_FLUSH, null, null)
                        showTextInputDialog = false

                        if (topViewModel.isLoggedIn) {
                            GeminiWord.extractTermsFromHistory(
                                history = messages.map { it.text } + inputText,
                                onResult = { terms ->
                                    wordsViewModel.addExtractedWords(talkId, terms, allWords)
                                },
                                onError = { error -> Log.e("TalkScreen", "Gemini抽出失敗: $error") }
                            )
                        }
                    },
                    suggestions = if (topViewModel.isLoggedIn) aiSuggestions else emptyList(),
                    isLoading = if (topViewModel.isLoggedIn) isGeneratingSuggestions else false
                )
            }
        }
    }
}
