package com.example.talkeasy.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 音声入力メッセージを表示する吹き出しUI
@Composable
fun VoiceMessageBubble(text: String, isCorrecting: Boolean = false) {
    // isCorrectingがtrueの場合、テキストにグラデーションアニメーションを適用
    val styledText = if (isCorrecting) {
        val transition = rememberInfiniteTransition(label = "correcting_animation")
        val offsetX by transition.animateFloat(
            initialValue = -300f, // 開始位置
            targetValue = 300f, // 終了位置
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = LinearEasing), // 3秒かけて線形に変化
                repeatMode = RepeatMode.Restart // 繰り返す
            ),
            label = "offset_x_animation"
        )

        val animatedBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF64B5F6),
                Color(0xFF42A5F5),
                Color(0xFF90CAF9)
            ),
            start = Offset(offsetX, 0f),
            end = Offset(offsetX + 300f, 0f)
        )

        buildAnnotatedString {
            withStyle(style = SpanStyle(brush = animatedBrush)) {
                append(text)
            }
        }
    } else {
        // isCorrectingがfalseの場合は、通常のテキスト
        buildAnnotatedString { append(text) }
    }

    // 吹き出しを左寄せで配置
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color.Gray), // グレーの枠線
            shape = RoundedCornerShape(20.dp), // 角を丸める
            modifier = Modifier.widthIn(max = 300.dp) // 最大幅を指定
        ) {
            Text(
                text = styledText, // アニメーション付きまたは通常のテキスト
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                // 補正中はスタイルに任せ、そうでなければ黒色
                color = if (isCorrecting) Color.Unspecified else Color.Black
            )
        }
    }
}

@Preview(showBackground = true, name = "補正中")
@Composable
fun VoiceMessageBubblePreviewCorrecting() {
    VoiceMessageBubble(text = "クラウドにデプロイして", isCorrecting = true)
}

@Preview(showBackground = true, name = "補正完了")
@Composable
fun VoiceMessageBubblePreviewFinal() {
    VoiceMessageBubble(text = "クラウドにデプロイして", isCorrecting = false)
}
