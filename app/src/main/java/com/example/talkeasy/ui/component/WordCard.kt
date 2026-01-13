package com.example.talkeasy.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.talkeasy.data.entity.Words

// 単語情報を表示するカードUI
@Composable
fun WordCard(
    word: Words, // 表示する単語
    onClick: (Words) -> Unit // カードクリック時の処理
) {
    // クリック可能なカードUI
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onClick(word) } // クリック時にonClick処理を呼び出す
    ) {
        // カード内の要素を横に並べる
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // 要素を両端に配置
        ) {
            // 左側: 単語を表示
            Text(
                text = word.word,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 右側: 単語の読み仮名を表示
            Text(
                text = word.wordRuby,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
