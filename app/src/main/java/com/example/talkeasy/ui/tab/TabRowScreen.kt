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
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.screen.TalksScreen
import com.example.talkeasy.ui.screen.TopScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabRowScreen(modifier: Modifier = Modifier, initialTabIndex: Int = 0) {
    val navController = LocalNavController.current
    var tabIndex by remember { mutableStateOf(initialTabIndex) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (tabIndex) {
            0 -> TopScreen(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope
            )
            1 -> TalksScreen(
                onTalkClick = { talk ->
                    navController.navigate("talk/${talk.id}")
                }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 👇 SnackbarHost をタブの前に配置（オーバーレイ表示）
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.fillMaxWidth()
            )

            // タブはそのまま下に配置
            TabRowView(tabIndex = tabIndex, onTabChange = { tabIndex = it })
        }
    }
}
