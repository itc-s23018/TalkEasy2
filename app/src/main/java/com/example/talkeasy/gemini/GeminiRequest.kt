package com.example.talkeasy.gemini

import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.entity.Words

data class GeminiRequest(
    val idToken: String, //認証用のIDトークン
    val prompt: String, //Geminiに送信するプロンプト
    val mode: String = "voice", //Geminiの動作モード（例: "voice"）
    val history: List<String> = emptyList(), //会話の履歴
    val dbWords: List<Words> = emptyList(), //データベースから取得した単語のリスト
    val user: User? = null //ユーザー情報
)
