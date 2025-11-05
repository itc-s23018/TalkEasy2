
package com.example.talkeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.talkeasy.ui.screen.TopScreen
import com.example.talkeasy.ui.theme.TalkEasyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TalkEasyTheme {
                TopScreen()
            }
        }
    }
}
