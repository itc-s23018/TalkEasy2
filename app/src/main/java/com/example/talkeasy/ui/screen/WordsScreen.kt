package com.example.talkeasy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.talkeasy.ui.theme.TalkEasyTheme

@Composable
fun WordsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("マイ辞書")
    }
}

@Preview(showBackground = true)
@Composable
fun WordsScreenPreview() {
    TalkEasyTheme {
        WordsScreen()
    }
}