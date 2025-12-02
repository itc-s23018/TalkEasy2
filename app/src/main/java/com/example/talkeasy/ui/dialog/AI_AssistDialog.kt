package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    onDismiss: () -> Unit,
    onToggle: (Boolean) -> Unit // ← 切り替えた瞬間に呼び出し元へ通知
) {
    var aiEnabled by remember { mutableStateOf(false) }

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

                // トグルスイッチ（明るい青）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("AIアシストを有効化", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = aiEnabled,
                        onCheckedChange = {
                            aiEnabled = it
                            onToggle(it) // ← 切り替えた瞬間に保存処理を呼び出す
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF42A5F5), // 明るい青 (Light Blue 400)
                            checkedTrackColor = Color(0xFF90CAF9), // トラックも淡い青
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 注意事項（赤文字の箇条書き）
                Text(
                    "Google アカウントとの連携が必要です",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AI_AssistDialogPreview() {
    Surface {
        AI_AssistDialog(
            onDismiss = {},
            onToggle = { enabled -> println("AIアシスト: $enabled") }
        )
    }
}


