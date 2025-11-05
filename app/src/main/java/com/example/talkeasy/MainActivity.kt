package com.example.talkeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.screen.TalkScreen
import com.example.talkeasy.ui.screen.TalksScreen
import com.example.talkeasy.ui.screen.WordsScreen
import com.example.talkeasy.ui.tab.TabRowScreen
import com.example.talkeasy.ui.theme.TalkEasyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TalkEasyTheme {
                val navController = rememberNavController()

                CompositionLocalProvider(LocalNavController provides navController) {
                    NavHost(navController = navController, startDestination = "tabs") {
                        composable("tabs") { TabRowScreen() }
                        composable("words") { WordsScreen() }
                        composable("talks") { TalksScreen() }
                        composable("talk/{talkId}") { backStackEntry ->
                            val talkId = backStackEntry.arguments?.getString("talkId")?.toIntOrNull()
                            if (talkId != null) {
                                TalkScreen(talkId = talkId)
                            }
                        }
                    }
                }
            }
        }
    }
}