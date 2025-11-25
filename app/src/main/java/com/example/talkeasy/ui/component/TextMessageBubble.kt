package com.example.talkeasy.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.talkeasy.R

@Composable
fun TextMessageBubble(
    text: String,
    onSpeak: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF98FB98)), // きみどり背景
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.widthIn(max = 300.dp),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onSpeak(text) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.speaker),
                        contentDescription = "音声生成",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun PreviewTextMessageBubbleShort() {
    TextMessageBubble(text = "こんにちは！")
}

@Preview(showBackground = true)
@Composable
fun PreviewTextMessageBubbleLong() {
    TextMessageBubble(
        text = "これは長文のテストです。吹き出しの中でテキストが折り返されるか、" +
                "UI が崩れないかを確認するために、ある程度長い文章を入れています。"
    )
}
