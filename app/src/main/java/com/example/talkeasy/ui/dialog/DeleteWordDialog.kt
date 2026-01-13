package com.example.talkeasy.ui.dialog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.talkeasy.data.entity.Words

// 単語を削除する前に確認するためのダイアログ
@Composable
fun DeleteWordDialog(
    word: Words,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("用語の削除", color = Color.Black) },
        text = { Text("'${word.word}' を本当に削除しますか？", color = Color.Black) },
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
