package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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

@Composable
fun EditTilteDialog(
    initialTalkTitle: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var talkTitle by remember { mutableStateOf(initialTalkTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("タイトル編集", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        },

        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                OutlinedTextField(
                    value = talkTitle,
                    onValueChange = { talkTitle = it },
                    label = { Text("トークタイトル") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (talkTitle.isNotEmpty()
                    ) {
                        onConfirm(talkTitle)
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
                onClick = onDismiss,
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
        titleContentColor = Color.Black,
        textContentColor = Color.Black
    )
}

@Preview
@Composable
fun EditTitleDialogPreview() {
    EditTilteDialog(
        initialTalkTitle = "Test Title",
        onConfirm = {},
        onDismiss = {}
    )
}