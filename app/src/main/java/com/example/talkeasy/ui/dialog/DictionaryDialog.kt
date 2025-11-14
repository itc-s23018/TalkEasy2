package com.example.talkeasy.ui.dialog

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.talkeasy.data.entity.Words
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.gemini.GeminiWord
import com.example.talkeasy.ui.viewmodel.WordsViewModel

@Composable
fun DictionaryDialog(
    onDismiss: () -> Unit,
    words: List<Words>,
    categoryViewModel: CategoryViewModel,
    wordsViewModel: WordsViewModel,
    talkId: Int,
    allWords: List<Words>,
    messages: List<String>,
    onWordSaved: (Words) -> Unit
) {
    var selectedWord by remember { mutableStateOf<Words?>(null) }
    var isLoading by remember { mutableStateOf(false) } // ✅ ローディング状態

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 4.dp) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text("取得された専門用語", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        // ✅ ローディング表示
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    words.isEmpty() -> {
                        Text("まだ用語がありません")
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(words) { word ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = word.word, style = MaterialTheme.typography.bodyLarge)
                                            Text(text = word.wordRuby, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                        }
                                        Button(onClick = { selectedWord = word }) {
                                            Text("追加")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("閉じる")
                }
            }
        }
    }

    if (selectedWord != null) {
        InputWordDialog(
            categoryViewModel = categoryViewModel,
            initialWord = selectedWord!!.word,
            initialWordRuby = selectedWord!!.wordRuby,
            onConfirm = { word, ruby, category ->
                val savedWord = selectedWord!!.copy(word = word, wordRuby = ruby, category = category)
                onWordSaved(savedWord)
                wordsViewModel.removeExtractedWord(talkId, selectedWord!!)
                selectedWord = null
            },
            onDismiss = {
                // ✅ 閉じたら削除
                wordsViewModel.removeExtractedWord(talkId, selectedWord!!)
                selectedWord = null

                // ✅ ローディング開始
                isLoading = true
                GeminiWord.extractTermsFromHistory(
                    history = messages,
                    onResult = { terms ->
                        wordsViewModel.addExtractedWords(talkId, terms, allWords)
                        isLoading = false // ✅ 完了
                    },
                    onError = { error ->
                        Log.e("DictionaryDialog", "Gemini抽出失敗: $error")
                        isLoading = false // ✅ エラーでも終了
                    }
                )
            }
        )
    }
}
