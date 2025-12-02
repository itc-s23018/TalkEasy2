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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabRowScreen(modifier: Modifier = Modifier, initialTabIndex: Int = 0) {
    val navController = LocalNavController.current
    var tabIndex by remember { mutableStateOf(initialTabIndex) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    var drawerOpen by remember { mutableStateOf(false) }


    val vm: TopViewModel = hiltViewModel()

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

        RightDrawer(
            isOpen = drawerOpen,
            onClose = { drawerOpen = false },
            onUserEdit = {
                vm.showEditDialog()
                drawerOpen = false
            },
            onSetting = {
                // 今は何もしない
                drawerOpen = false
            },
            content = {}
        )
    }
}
