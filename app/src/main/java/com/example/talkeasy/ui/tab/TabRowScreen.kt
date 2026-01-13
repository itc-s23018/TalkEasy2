package com.example.talkeasy.ui.tab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.talkeasy.data.viewmodel.TopViewModel
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.component.RightDrawer
import com.example.talkeasy.ui.screen.TalksScreen
import com.example.talkeasy.ui.screen.TopScreen
import com.example.talkeasy.ui.dialog.AI_AssistDialog
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// アプリの主要な画面（トップ、トーク一覧）をタブで切り替えるためのコンテナ画面
@Composable
fun TabRowScreen(
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0,
    googleSignInClient: GoogleSignInClient,
    auth: FirebaseAuth,
    onLoginClick: () -> Unit
) {
    val navController = LocalNavController.current
    // 現在選択されているタブのインデックスを保持する状態変数
    var tabIndex by remember { mutableStateOf(initialTabIndex) }


    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    var drawerOpen by remember { mutableStateOf(false) }

    val vm: TopViewModel = hiltViewModel()
    val showAiAssistDialog = vm.showAiAssistDialog

    // 画面全体のレイアウト
    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // タブのインデックスに応じて表示する画面を切り替える
        when (tabIndex) {
            0 -> TopScreen(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onOpenDrawer = { drawerOpen = true },
                auth = auth,
                onLoginClick = { onLoginClick() }
            )
            1 -> TalksScreen(
                onTalkClick = { talk -> navController.navigate("talk/${talk.id}") }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.fillMaxWidth())
            TabRowView(tabIndex = tabIndex, onTabChange = { tabIndex = it })
        }

        RightDrawer(
            isOpen = drawerOpen,
            onClose = { drawerOpen = false },
            onUserEdit = { vm.showEditDialog(); drawerOpen = false },
            onSetting = { vm.openAiAssistDialog(); drawerOpen = false },
            content = {}
        )

        if (showAiAssistDialog) {
            if (vm.isLoggedIn) {
                AI_AssistDialog(
                    userEmail = vm.firebaseUser?.email,
                    userName = vm.firebaseUser?.displayName,
                    userPhotoUrl = vm.firebaseUser?.photoUrl?.toString(),
                    isLoggedIn = true,
                    onDismiss = { vm.dismissAiAssistDialog() },
                    onLoginClick = {  },
                    onLogoutClick = {
                        vm.logout {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Googleアカウントからログアウトしました")
                            }
                        }
                    }
                )
            } else {
                AI_AssistDialog(
                    userEmail = null,
                    userName = null,
                    userPhotoUrl = null,
                    isLoggedIn = false,
                    onDismiss = { vm.dismissAiAssistDialog() },
                    onLoginClick = { onLoginClick() },
                    onLogoutClick = { /* 未ログインのため何もしない */ }
                )
            }
        }
    }
}
