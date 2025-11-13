package com.example.talkeasy.ui.dialog

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.talkeasy.data.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWordDialog(
    categoryViewModel: CategoryViewModel,
    initialWord: String,
    initialRuby: String,
    initialCategory: String,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
){

    var editWord by remember { mutableStateOf(initialWord) }
    var editRuby by remember { mutableStateOf(initialRuby) }
    var editCategory by remember { mutableStateOf(initialCategory) }
    var expanded by remember { mutableStateOf(false) }


    val categories by categoryViewModel.categories.collectAsState()

    var showInputCategoryDialog by remember { mutableStateOf(false) }

    if (showInputCategoryDialog) {
        InputCategoryDialog(
            onConfirm = { newCategory ->
                categoryViewModel.addCategory(newCategory) // ✅ DBに保存
                editCategory = newCategory
                showInputCategoryDialog = false
            },
            onDismiss = { showInputCategoryDialog = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("用語を編集", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                OutlinedTextField(
                    value = editWord,
                    onValueChange = { editWord = it },
                    label = { Text("用語") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = editRuby,
                    onValueChange = { editRuby = it },
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
                        value = editCategory,
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
                                text = { Text(category.name) }, // ✅ DBの値を表示
                                onClick = {
                                    editCategory = category.name
                                    expanded = false
                                }
                            )
                        }
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
                    if (editWord.isNotEmpty() && editRuby.isNotEmpty() && editCategory.isNotEmpty()) {
                        onConfirm(editWord, editRuby, editCategory)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                modifier = Modifier.height(48.dp)
            ) {
                Text("更新")
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