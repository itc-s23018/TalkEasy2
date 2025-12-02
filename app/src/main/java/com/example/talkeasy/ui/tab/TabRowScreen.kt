package com.example.talkeasy.ui.tab

import android.os.Build
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabRowScreen(modifier: Modifier = Modifier, initialTabIndex: Int = 0) {
    val navController = LocalNavController.current
    var tabIndex by remember { mutableStateOf(initialTabIndex) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var drawerOpen by remember { mutableStateOf(false) }

    val vm: TopViewModel = hiltViewModel()
    val user = vm.user
    val showAiAssistDialog = vm.showAiAssistDialog

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // メイン画面（タブ切り替え）
        when (tabIndex) {
            0 -> TopScreen(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onOpenDrawer = { drawerOpen = true }
            )
            1 -> TalksScreen(
                onTalkClick = { talk ->
                    navController.navigate("talk/${talk.id}")
                }
            )
        }

        // タブとSnackbar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.fillMaxWidth()
            )
            TabRowView(tabIndex = tabIndex, onTabChange = { tabIndex = it })
        }

        // 右ドロワー
        RightDrawer(
            isOpen = drawerOpen,
            onClose = { drawerOpen = false },
            onUserEdit = {
                vm.showEditDialog()
                drawerOpen = false
            },
            onSetting = {
                vm.openAiAssistDialog()
                drawerOpen = false
            },
            content = {}
        )

        // AIアシストダイアログ
        if (showAiAssistDialog && user != null) {
            AI_AssistDialog(
                aiEnabledInitial = user.aiAssist,   // ← ユーザー情報から初期値を渡す
                onDismiss = { vm.dismissAiAssistDialog() },
                onToggle = { enabled ->
                    vm.updateAiAssist(enabled)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            if (enabled) "AIアシストを有効化しました"
                            else "AIアシストを無効化しました"
                        )
                    }
                }
            )
        }
    }
}
