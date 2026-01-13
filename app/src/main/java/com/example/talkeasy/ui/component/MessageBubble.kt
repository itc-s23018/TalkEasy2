package com.example.talkeasy.ui.component

import androidx.compose.runtime.Composable
import com.example.talkeasy.data.entity.InputType

@Composable
fun MessageBubble(
    text: String, // 表示するテキスト
    inputType: InputType, // 入力タイプ (テキスト or 音声)
    onSpeak: (String) -> Unit = {} // テキストを読み上げるための関数
) {
    // 入力タイプに応じて表示するコンポーネントを切り替える
    when (inputType) {
        InputType.TEXT -> TextMessageBubble(text = text, onSpeak = onSpeak) // テキスト入力の場合
        InputType.VOICE -> VoiceMessageBubble(text = text) // 音声入力の場合
    }
}
