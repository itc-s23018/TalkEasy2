package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talkeasy.ui.theme.TalkEasyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(
    initialLastName: String,
    initialFirstName: String,
    initialLastNameRudy: String,
    initialFirstNameRudy: String,
    onConfirm: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputLastName by rememberSaveable { mutableStateOf(initialLastName) }
    var inputFirstName by rememberSaveable { mutableStateOf(initialFirstName) }
    var inputLastNameRudy by rememberSaveable { mutableStateOf(initialLastNameRudy) }
    var inputFirstNameRudy by rememberSaveable { mutableStateOf(initialFirstNameRudy) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("ユーザー編集", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = inputLastName,
                        onValueChange = { inputLastName = it },
                        label = { Text("姓") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = inputFirstName,
                        onValueChange = { inputFirstName = it },
                        label = { Text("名") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = inputLastNameRudy,
                        onValueChange = { inputLastNameRudy = it },
                        label = { Text("セイ") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = inputFirstNameRudy,
                        onValueChange = { inputFirstNameRudy = it },
                        label = { Text("メイ") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputLastName.isNotEmpty() && inputFirstName.isNotEmpty() &&
                        inputLastNameRudy.isNotEmpty() && inputFirstNameRudy.isNotEmpty()
                    ) {
                        onConfirm(inputLastName, inputFirstName, inputLastNameRudy, inputFirstNameRudy)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                modifier = Modifier.height(48.dp)
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
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

@Preview(showBackground = true)
@Composable
fun EditUserDialogPreview() {
    TalkEasyTheme {
        EditUserDialog(
            initialLastName = "德里",
            initialFirstName = "政士郎",
            initialLastNameRudy = "トクザト",
            initialFirstNameRudy = "セイシロウ",
            onConfirm = { _, _, _, _ -> },
            onDismiss = {}
        )
    }
}