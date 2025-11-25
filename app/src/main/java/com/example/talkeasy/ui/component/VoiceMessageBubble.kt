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

@Composable
fun VoiceMessageBubble(text: String, isCorrecting: Boolean = false) {
    // 補正中ならグラデーションアニメーション
    val styledText = if (isCorrecting) {
        val transition = rememberInfiniteTransition(label = "")
        val offsetX by transition.animateFloat(
            initialValue = -300f,
            targetValue = 300f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = ""
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
        buildAnnotatedString { append(text) }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color.Gray),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = styledText,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
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
