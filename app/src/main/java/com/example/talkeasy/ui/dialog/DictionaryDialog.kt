package com.example.talkeasy.ui.dialog

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

@Composable
fun DictionaryDialog(
    onDismiss: () -> Unit,
    words: List<Words>,
    categoryViewModel: CategoryViewModel,
    onWordSaved: (Words) -> Unit
) {
    var selectedWord by remember { mutableStateOf<Words?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 4.dp) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text("取得された専門用語", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                if (words.isEmpty()) {
                    Text("まだ用語がありません")
                } else {
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
                selectedWord = null
            },
            onDismiss = { selectedWord = null }
        )
    }
}
