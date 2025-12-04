package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter

@Composable
fun AI_AssistDialog(
    userEmail: String?,
    userName: String?,
    userPhotoUrl: String?,
    isLoggedIn: Boolean,
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // タイトル
                Text("AIアシスト設定", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                // アカウント情報表示
                if (isLoggedIn && userEmail != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (userPhotoUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(userPhotoUrl),
                                contentDescription = "Google Account Photo",
                                modifier = Modifier.size(64.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (userName != null) {
                            Text(userName, style = MaterialTheme.typography.bodyLarge)
                        }
                        Text(userEmail, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                } else {
                    Text(
                        "Google アカウントとの連携が必要です",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ログイン／ログアウトボタン
                if (!isLoggedIn) {
                    Button(
                        onClick = onLoginClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Googleアカウントと連携する")
                    }
                } else {
                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Googleアカウント連携を解除する")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 閉じるボタン
                TextButton(onClick = onDismiss) {
                    Text("閉じる")
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Logged In")
@Composable
fun AI_AssistDialog_LoggedInPreview() {
    Surface {
        AI_AssistDialog(
            userEmail = "test.user@example.com",
            userName = "Test User",
            userPhotoUrl = "https://example.com/photo.jpg",
            isLoggedIn = true,
            onDismiss = {},
            onLoginClick = {},
            onLogoutClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Logged Out")
@Composable
fun AI_AssistDialog_LoggedOutPreview() {
    Surface {
        AI_AssistDialog(
            userEmail = null,
            userName = null,
            userPhotoUrl = null,
            isLoggedIn = false,
            onDismiss = {},
            onLoginClick = {},
            onLogoutClick = {}
        )
    }
}
