package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talkeasy.ui.theme.TalkEasyTheme

@Composable
fun InputCategoryDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputNewCategory by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "カテゴリを追加",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            OutlinedTextField(
                value = inputNewCategory,
                onValueChange = { inputNewCategory = it },
                label = { Text("カテゴリ名") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmed = inputNewCategory.trim()
                    if (trimmed.isNotEmpty()) {
                        onConfirm(trimmed)
                        inputNewCategory = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    inputNewCategory = "" // ✅ キャンセル時もクリア
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Text("キャンセル")
            }
        },
        containerColor = Color.White,
    )
}


@Preview(showBackground = true)
@Composable
fun InputCategoryDialogPreview() {
    TalkEasyTheme {
        Surface {
            InputCategoryDialog(
                onConfirm = { _ -> },
                onDismiss = {}
            )
        }
    }
}
