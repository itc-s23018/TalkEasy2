package com.example.talkeasy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.ui.theme.TalkEasyTheme

@Composable
fun MessagesButton(
    onVoiceInputClick: () -> Unit,
    onKeyboardInputClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onVoiceInputClick,
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.voice_input),
                    contentDescription = "音声入力",
                    modifier = Modifier.size(45.dp),
                    tint = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(Color.Black)
            )

            Button(
                onClick = onKeyboardInputClick,
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.keyboard),
                    contentDescription = "キーボード入力",
                    modifier = Modifier.size(45.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessagesButtonPreview() {
    TalkEasyTheme {
        MessagesButton(
            onVoiceInputClick = {},
            onKeyboardInputClick = {}
        )
    }
}
