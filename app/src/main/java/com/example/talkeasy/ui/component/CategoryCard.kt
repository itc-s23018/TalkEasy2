package com.example.talkeasy.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.talkeasy.data.entity.Category

@Composable
fun CategoryCard(
    category: Category, // 表示するカテゴリ
    wordCount: Int,     // カテゴリに含まれる単語数
    onClick: (Category) -> Unit = {} // カードクリック時の処理
) {
    // クリック可能なカードUI
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onClick(category) } // クリック時にonClick処理を呼び出す
    ) {
        // カード内の要素を横に並べる
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // 要素を両端に配置
        ) {
            // 左側: カテゴリ名を表示
            Text(
                text = category.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 右側: 紐付けられた用語数を表示
            Text(
                text = "$wordCount 件",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
