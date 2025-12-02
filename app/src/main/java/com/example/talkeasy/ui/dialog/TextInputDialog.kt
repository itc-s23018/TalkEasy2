package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.talkeasy.R

@Composable
fun TextInputDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    suggestions: List<String> = emptyList(),
    isLoading: Boolean = false
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White,
        title = { Text("テキスト入力", fontSize = 20.sp) },
        text = {
            Column {
                // 入力欄
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("内容を入力") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                if (suggestions.isNotEmpty() || isLoading) {
                    Text("言いたいことはこれ？", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        suggestions.forEach {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { text = it },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Text(
                                    text = it,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "送信",
                    tint = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Text("キャンセル", fontSize = 16.sp)
            }
        }
    )
}


@Preview(showBackground = true, name = "TextInputDialog Preview (有効時)")
@Composable
fun TextInputDialogPreviewEnabled() {
    TextInputDialog(
        onDismissRequest = {},
        onConfirm = {},
        suggestions = listOf(
            "それは面白いですね！",
            "もっと詳しく教えてください！",
            "それについてどう思いますか？"
        ),
        isLoading = false
    )
}

@Preview(showBackground = true, name = "TextInputDialog Preview (無効時)")
@Composable
fun TextInputDialogPreviewDisabled() {
    TextInputDialog(
        onDismissRequest = {},
        onConfirm = {},
        suggestions = emptyList(),
        isLoading = false         
    )
}