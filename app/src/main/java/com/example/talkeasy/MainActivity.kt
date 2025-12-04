package com.example.talkeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.talkeasy.ui.LocalNavController
import com.example.talkeasy.ui.screen.TalkScreen
import com.example.talkeasy.ui.screen.WordsScreen
import com.example.talkeasy.ui.tab.TabRowScreen
import com.example.talkeasy.ui.theme.TalkEasyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.talkeasy.data.viewmodel.CategoryViewModel
import com.example.talkeasy.data.viewmodel.TopViewModel
import com.example.talkeasy.ui.screen.CategoriesScreen
import com.example.talkeasy.ui.viewmodel.WordsViewModel

// Firebase / Google Sign-In
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // Googleログイン結果を受け取るランチャー
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    val vm: TopViewModel = ViewModelProvider(this)[TopViewModel::class.java]
                    // 👇 ログイン処理と同時に idToken を保存
                    vm.loginWithGoogle(
                        idToken,
                        onSuccess = {
                            vm.saveIdToken(idToken)
                        },
                        onError = { e -> e.printStackTrace() }
                    )

                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            TalkEasyTheme {
                val navController = rememberNavController()

                CompositionLocalProvider(LocalNavController provides navController) {
                    NavHost(
                        navController = navController,
                        startDestination = "tabs/0"
                    ) {
                        composable(
                            "tabs/{tabIndex}",
                            arguments = listOf(navArgument("tabIndex") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val tabIndex = backStackEntry.arguments?.getInt("tabIndex") ?: 0
                            TabRowScreen(
                                modifier = Modifier,
                                initialTabIndex = tabIndex,
                                googleSignInClient = googleSignInClient,
                                auth = auth,
                                onLoginClick = {
                                    val signInIntent = googleSignInClient.signInIntent
                                    signInLauncher.launch(signInIntent)
                                }
                            )
                        }

                        composable("talk/{talkId}") { backStackEntry ->
                            val talkId = backStackEntry.arguments?.getString("talkId")?.toIntOrNull()
                            if (talkId != null) {
                                TalkScreen(talkId = talkId)
                            }
                        }

                        composable("words") {
                            val wordsViewModel: WordsViewModel = hiltViewModel()
                            val categoryViewModel: CategoryViewModel = hiltViewModel()
                            WordsScreen(
                                viewModel = wordsViewModel,
                                categoryViewModel = categoryViewModel,
                                navController = navController,
                                onBackClick = { navController.navigate("tabs/0") }
                            )
                        }

                        composable("category_list") {
                            val categoryViewModel: CategoryViewModel = hiltViewModel()
                            CategoriesScreen(
                                categoryViewModel = categoryViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
