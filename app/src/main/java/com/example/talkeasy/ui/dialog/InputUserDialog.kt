package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputUserDialog(
    onConfirm: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputLastName by remember { mutableStateOf("") }
    var inputLastNameRudy by remember { mutableStateOf("") }
    var inputFirstName by remember { mutableStateOf("") }
    var inputFirstNameRudy by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
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
                Text("ユーザー登録", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(10.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (inputLastName.isNotEmpty() && inputFirstName.isNotEmpty() &&
                                inputLastNameRudy.isNotEmpty() && inputFirstNameRudy.isNotEmpty()
                            ) {
                                onConfirm(inputLastName, inputFirstName, inputLastNameRudy, inputFirstNameRudy)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("登録")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun InputUserDialogPreview() {
    Surface {
        InputUserDialog(onConfirm = { _, _, _, _ -> }, onDismiss = {})
    }
}