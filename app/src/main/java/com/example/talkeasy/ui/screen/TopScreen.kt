package com.example.talkeasy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.viewmodel.TopViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.dialog.EditUserDialog
import com.example.talkeasy.ui.dialog.InputUserDialog
import com.example.talkeasy.ui.dialog.AI_AssistDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopScreen(
    viewModel: TopViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onOpenDrawer: () -> Unit,
    auth: FirebaseAuth,                // 👈 追加: FirebaseAuthを渡す
    onLoginClick: () -> Unit           // 👈 追加: MainActivityから渡す
) {
    val navController = LocalNavController.current
    val user = viewModel.user
    val showDialog = viewModel.showUserInputDialog
    val showEditDialog = viewModel.showUserEditDialog
    val showAiAssistDialog = viewModel.showAiAssistDialog

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.displaySmall
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    if (user != null) {
                        Text("こんにちは、${user.lastName}さん", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // トーク開始ボタン
                    Button(
                        onClick = {
                            val talkId = UUID.randomUUID().toString()
                            val title = "新しいトーク $talkId"
                            viewModel.createNewTalk(title) { createdId ->
                                navController.navigate("talk/$createdId")
                            }
                        },
                        modifier = Modifier.size(width = 300.dp, height = 130.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF6495ED), Color(0xFF5CACEE))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("トークを始める", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.talk),
                                    contentDescription = "Talk",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // マイ辞書ボタン
                    Button(
                        onClick = { navController.navigate("words") },
                        modifier = Modifier.size(width = 300.dp, height = 130.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFF08080), Color(0xFFCD5C5C))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("マイ辞書", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.dictionary),
                                    contentDescription = "Dictionary",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = { onOpenDrawer() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 30.dp, end = 10.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = "Menu",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }

    // ユーザー登録ダイアログ
    if (showDialog) {
        InputUserDialog(
            onConfirm = { lastName, firstName, lastNameRuby, firstNameRuby ->
                viewModel.registerUser(
                    User(
                        lastName = lastName,
                        firstName = firstName,
                        lastNameRuby = lastNameRuby,
                        firstNameRuby = firstNameRuby
                    )
                )
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("ユーザー情報を登録しました")
                }
            },
            onDismiss = { viewModel.dismissDialog() }
        )
    }

    // ユーザー編集ダイアログ
    if (showEditDialog && user != null) {
        EditUserDialog(
            initialLastName = user.lastName,
            initialFirstName = user.firstName,
            initialLastNameRudy = user.lastNameRuby,
            initialFirstNameRudy = user.firstNameRuby,
            onConfirm = { ln, fn, lnr, fnr ->
                viewModel.updateUser(ln, fn, lnr, fnr)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("ユーザー情報を更新しました")
                }
            },
            onDismiss = { viewModel.dismissEditDialog() }
        )
    }
}
