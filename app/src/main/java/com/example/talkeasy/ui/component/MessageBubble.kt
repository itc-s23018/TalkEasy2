package com.example.talkeasy.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.InputType

@Composable
fun MessageBubble(text: String, inputType: InputType) {
    val (isUser, bubbleColor) = when (inputType) {
        InputType.TEXT -> true to Color(0xFFDCF8C6)
        InputType.VOICE -> false to Color(0xFFBBDEFB)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 4.dp) // ← 1文字分の余白
                )

                if (inputType == InputType.TEXT) {
                    IconButton(onClick = { /* 再生処理など */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.speaker),
                            contentDescription = "音声生成",
                            modifier = Modifier.size(35.dp),
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}