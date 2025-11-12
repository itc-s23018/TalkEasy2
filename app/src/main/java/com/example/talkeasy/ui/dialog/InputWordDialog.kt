package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talkeasy.ui.theme.TalkEasyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputWordDialog(
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputWord by remember { mutableStateOf("") }
    var inputWordRuby by remember { mutableStateOf("") }
    var inputCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // カテゴリリスト
    val categories = remember { mutableStateListOf<String>() }

    var showInputCategoryDialog by remember { mutableStateOf(false) }

    // ▼ カテゴリ追加ダイアログの表示制御
    if (showInputCategoryDialog) {
        InputCategoryDialog(
            onConfirm = { newCategory ->
                categories.add(newCategory)
                inputCategory = newCategory
                showInputCategoryDialog = false
            },
            onDismiss = { showInputCategoryDialog = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("用語を追加", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                OutlinedTextField(
                    value = inputWord,
                    onValueChange = { inputWord = it },
                    label = { Text("用語") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = inputWordRuby,
                    onValueChange = { inputWordRuby = it },
                    label = { Text("ルビ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ▼ カテゴリ選択欄
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = inputCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("カテゴリ選択") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    inputCategory = category
                                    expanded = false
                                }
                            )
                        }
                        // ▼ 追加項目
                        DropdownMenuItem(
                            text = { Text("+カテゴリを追加") },
                            onClick = {
                                expanded = false
                                showInputCategoryDialog = true
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputWord.isNotEmpty() && inputWordRuby.isNotEmpty() && inputCategory.isNotEmpty()) {
                        onConfirm(inputWord, inputWordRuby, inputCategory)
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
    )
}

@Preview(showBackground = true)
@Composable
fun InputWordDialogPreview() {
    TalkEasyTheme {
        Surface {
            // プレビュー用に常にダイアログを表示
            InputWordDialog(
                onConfirm = { _, _, _ -> },
                onDismiss = {}
            )
        }
    }
}



