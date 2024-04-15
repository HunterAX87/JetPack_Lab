package com.example.jetpack_test

import android.widget.Toast
import android.widget.Toast.makeText
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch



@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){

    val scope= rememberCoroutineScope()
    val pagerState= rememberPagerState(pageCount = {HomeTabs.entries.size})
    val selectedTabIndex= remember { derivedStateOf { pagerState.currentPage } }

    Scaffold{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier= Modifier.fillMaxWidth()) {
                HomeTabs.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex.value==index,
                        selectedContentColor = Color.Green,
                        unselectedContentColor= Color.Gray,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTab.ordinal)
                            }
                        },
                        text = { Text(text = currentTab.text)}
                    )
                }
            }
            
            HorizontalPager(
                state = pagerState,
                modifier= Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {page ->
                when(page){
                    0-> SignInScreen()
                    1-> SignUpScreen()
                }
            }
        }
    }
}

@Composable
fun SignInScreen() {
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.6f)
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = login.value,
            onValueChange = {
                if (it.length <= 18) {
                    login.value = it.take(18)
                }
            },
            visualTransformation = VisualTransformation.None,
            label = { Text("Login") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        TextField(
            value = password.value,
            onValueChange = {
                if (it.length <= 18) {
                    password.value = it.take(18)
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),

        ) {
            Text(text = "SignIn")
        }
    }
}

@Composable
fun SignUpScreen() {
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email.value,
            onValueChange = {
                if (it.length <= 18) {
                    email.value = it.take(18)
                }
            },
            visualTransformation = VisualTransformation.None,
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        TextField(
            value = login.value,
            onValueChange = {
                if (it.length <= 18) {
                    login.value = it.take(18)
                }
            },
            visualTransformation = VisualTransformation.None,
            label = { Text("Login") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        TextField(
            value = password.value,
            onValueChange = {
                if (it.length <= 18) {
                    password.value = it.take(18)
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),

            ) {
            Text(text = "SignUp")
        }
    }
}



enum class HomeTabs(

    val text:String
){
    SignIn(
        text = "SignIn"
    ),
    SignUp(
        text = "SignUp"
    )
}