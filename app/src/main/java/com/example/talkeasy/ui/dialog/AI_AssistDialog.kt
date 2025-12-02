package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AI_AssistDialog(
    aiEnabledInitial: Boolean,   // ← ユーザー情報から渡す
    onDismiss: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    var aiEnabled by remember { mutableStateOf(aiEnabledInitial) } // ← 初期値を反映

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            color = Color.White,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // タイトル
                Text(
                    "AIアシスト設定",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 本文
                Text(
                    "AIを有効化すると、\n会話がより自然で豊かになります。",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // トグルスイッチ（横に「AIを有効化」と固定表示）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("AIを有効化", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = aiEnabled,
                        onCheckedChange = {
                            aiEnabled = it
                            onToggle(it) // ← 呼び出し元に通知
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF42A5F5),
                            checkedTrackColor = Color(0xFF90CAF9),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 注意事項
                Text(
                    "Google アカウントとの連携が必要です",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("閉じる")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AI_AssistDialogPreview() {
    Surface {
        AI_AssistDialog(
            aiEnabledInitial = true, // ← プレビュー用に true を渡す
            onDismiss = {},
            onToggle = { enabled -> println("AIアシスト: $enabled") }
        )
    }
}
