package com.example.talkeasy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R
import com.example.talkeasy.data.entity.User
import com.example.talkeasy.data.viewmodel.TopViewModel
import com.example.talkeasy.ui.dialog.EditUserDialog
import com.example.talkeasy.ui.dialog.InputUserDialog

@Composable
fun TopScreen(viewModel: TopViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
    val user = viewModel.user
    val showDialog = viewModel.showUserInputDialog
    val showEditDialog = viewModel.showUserEditDialog
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(50.dp))

            if (user != null) {
                Text("こんにちは、${user.lastName}さん", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = { /* TODO: トーク画面へ遷移 */ },
                modifier = Modifier.size(width = 300.dp, height = 130.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF6495ED), Color(0xFF5CACEE))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("トークを始める", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.talk),
                            contentDescription = "Talk",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: 辞書画面へ遷移 */ },
                modifier = Modifier.size(width = 300.dp, height = 130.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFF08080), Color(0xFFCD5C5C))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("マイ辞書", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.dictionary),
                            contentDescription = "Dictionary",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = { viewModel.showEditDialog() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 50.dp, end = 10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.account),
                contentDescription = "Account",
                tint = Color.Black,
                modifier = Modifier.size(48.dp)
            )
        }

        if (showDialog) {
            InputUserDialog(
                onConfirm = { lastName, firstName, lastNameRuby, firstNameRuby ->
                    viewModel.registerUser(
                        User(
                            lastName = lastName,
                            firstName = firstName,
                            lastNameRuby = lastNameRuby,
                            firstNameRuby = firstNameRuby
                        )
                    )
                },
                onDismiss = { viewModel.dismissDialog() }
            )
        }

        if (showEditDialog && user != null) {
            EditUserDialog(
                initialLastName = user.lastName,
                initialFirstName = user.firstName,
                initialLastNameRudy = user.lastNameRuby,
                initialFirstNameRudy = user.firstNameRuby,
                onConfirm = { ln, fn, lnr, fnr ->
                    viewModel.updateUser(ln, fn, lnr, fnr)
                },
                onDismiss = { viewModel.dismissEditDialog() }
            )
        }
    }
}