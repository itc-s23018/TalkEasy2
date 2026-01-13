package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.talkeasy.data.entity.Words

// カテゴリを削除する前に確認するためのダイアログ
@Composable
fun DeleteCategoryDialog(
    categoryName: String,
    words: List<Words>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("カテゴリ削除") },
        text = {
            Column {
                Text(
                    text = "「$categoryName」を本当に削除しますか？",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                // カテゴリに単語が含まれているかどうかに応じてメッセージを分岐
                if (words.isEmpty()) {
                    Text("このカテゴリには用語が保存されていません。", color = Color.Gray)
                } else {
                    Text(
                        text = "「$categoryName」には以下の用語が保存されています：",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // 含まれている単語のリストを表示
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp), // 高さに上限を設定
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(words) { word ->
                            Text("・${word.word} (${word.wordRuby})")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
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
                onClick = onDismiss,
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
