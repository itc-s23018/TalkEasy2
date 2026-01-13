package com.example.talkeasy

import android.os.Bundle
import android.util.Log
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
import com.example.talkeasy.data.viewmodel.WordsViewModel
import com.example.talkeasy.ui.screen.CategoriesScreen
// Firebase / Google Sign-In
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Firebase認証のインスタンス
    private lateinit var auth: FirebaseAuth
    // Googleサインインのためのクライアント
    private lateinit var googleSignInClient: GoogleSignInClient

    // Googleサインイン画面からの結果を受け取るためのランチャー
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Googleサインインに成功
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    // ViewModelを取得してFirebaseでのログイン処理を実行
                    val vm: TopViewModel = ViewModelProvider(this)[TopViewModel::class.java]
                    vm.loginWithGoogle(
                        idToken,
                        onSuccess = {
                            // 成功した場合、後続のAPIリクエストで利用するためにIDトークンを保存
                            vm.saveIdToken(idToken)
                        },
                        onError = { e ->
                            // エラーログを出力
                            Log.e("MainActivity", "Firebase Google Auth Failed", e)
                        }
                    )
                }
            } catch (e: ApiException) {
                // Googleサインインに失敗
                Log.e("MainActivity", "Google Sign In Failed", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase認証とGoogleサインインクライアントの初期化
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // WebクライアントIDを指定
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            TalkEasyTheme {
                // ナビゲーションコントローラーの初期化
                val navController = rememberNavController()

                // CompositionLocalProviderを使い、NavHost内でnavControllerをどこからでも参照できるようにする
                CompositionLocalProvider(LocalNavController provides navController) {
                    // NavHostで画面遷移を定義
                    NavHost(
                        navController = navController,
                        startDestination = "tabs/0" // 起動時の画面
                    ) {
                        // メインのタブ画面（トップ、トーク履歴）
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
                                    // Googleサインイン画面を起動
                                    val signInIntent = googleSignInClient.signInIntent
                                    signInLauncher.launch(signInIntent)
                                }
                            )
                        }

                        // 個別のトーク画面
                        composable("talk/{talkId}") { backStackEntry ->
                            val talkId = backStackEntry.arguments?.getString("talkId")?.toIntOrNull()
                            if (talkId != null) {
                                TalkScreen(talkId = talkId)
                            }
                        }

                        // マイ辞書画面
                        composable("words") {
                            val wordsViewModel: WordsViewModel = hiltViewModel()
                            val categoryViewModel: CategoryViewModel = hiltViewModel()
                            WordsScreen(
                                viewModel = wordsViewModel,
                                categoryViewModel = categoryViewModel,
                                navController = navController,
                                onBackClick = { navController.navigate("tabs/0") } // トップタブに戻る
                            )
                        }

                        // カテゴリ管理画面
                        composable("category_list") {
                            val categoryViewModel: CategoryViewModel = hiltViewModel()
                            CategoriesScreen(
                                categoryViewModel = categoryViewModel,
                                onBackClick = { navController.popBackStack() } // 前の画面に戻る
                            )
                        }
                    }
                }
            }
        }
    }
}
