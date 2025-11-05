package com.example.talkeasy.ui.tab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.talkeasy.data.viewmodel.TalksViewModel
import com.example.talkeasy.ui.screen.TalksScreen
import com.example.talkeasy.ui.screen.TopScreen
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun TabRowScreen(
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0,
    talksViewModel: TalksViewModel = hiltViewModel()
) {
    var tabIndex by remember { mutableStateOf(initialTabIndex) }
    val talks by talksViewModel.talks.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (tabIndex) {
            0 -> TopScreen()
            1 -> TalksScreen(
                talks = talks,
                onTalkClick = { talk -> /* 画面遷移など */ },
                onTalkDelete = { talk -> talksViewModel.deleteTalk(talk) },
                getDaysUntilExpiry = { talk ->
                    val expiryDate = talk.createdAt.plusWeeks(1)
                    val now = LocalDateTime.now()
                    Duration.between(now, expiryDate).toDays()
                }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRowView(tabIndex = tabIndex, onTabChange = { tabIndex = it })
        }
    }
}
