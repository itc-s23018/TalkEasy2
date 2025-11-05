package com.example.talkeasy.ui.tab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.talkeasy.ui.screen.TalksScreen
import com.example.talkeasy.ui.screen.TopScreen

@Composable
fun TabRowScreen(modifier: Modifier = Modifier) {
    var tabIndex by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (tabIndex) {
            0 -> TopScreen() // navController は CompositionLocal から取得
            1 -> TalksScreen()
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp), // 下に余白を開ける
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRowView(tabIndex = tabIndex, onTabChange = { tabIndex = it })
        }
    }
}