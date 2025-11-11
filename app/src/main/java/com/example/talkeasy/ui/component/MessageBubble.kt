package com.example.talkeasy.ui.component

import androidx.compose.runtime.Composable
import com.example.talkeasy.data.entity.InputType

@Composable
fun MessageBubble(
    text: String,
    inputType: InputType,
    onSpeak: (String) -> Unit = {}
) {
    when (inputType) {
        InputType.TEXT -> TextMessageBubble(text = text, onSpeak = onSpeak)
        InputType.VOICE -> VoiceMessageBubble(text = text)
    }
}
