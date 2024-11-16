package com.example.jetpack_test.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpack_test.R
import com.example.jetpack_test.db.Dao
import com.example.jetpack_test.db.UsersEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignInUpScreen(userDao: Dao, context: Context, navController: NavController, userName: String) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Scaffold { innerPadding -> // Используем innerPadding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Применяем отступы
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "user_img",
                modifier = Modifier
                    .fillMaxWidth(0.2f) // Устанавливаем ширину изображения
                    .fillMaxHeight(0.2f) // Устанавливаем высоту изображения
            )

            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    // Здесь мы определяем индикатор
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]),
                        color = colorResource(id = R.color.blue) // Задаем цвет подчеркивания
                    )
                }
            ) {
                HomeTabs.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        selectedContentColor = colorResource(id = R.color.blue),
                        unselectedContentColor = Color.Gray,

                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTab.ordinal)
                            }
                        },
                        text = { Text(text = currentTab.text) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> SignInState(userDao, context, navController, userName)
                    1 -> SignUpState(userDao, context, navController, userName)
                }
            }
        }
    }
}


@Composable
fun SignInState(userDao: Dao, context: Context, navController: NavController, userName: String) {
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.99f)
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
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.skyBlue),
                unfocusedContainerColor = colorResource(id = R.color.skyBlue),
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black,
                focusedTextColor = Color.Black
            )
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
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.skyBlue),
                unfocusedContainerColor = colorResource(id = R.color.skyBlue),
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black,
                focusedTextColor = Color.Black
            )
        )
        Button(
            onClick = {
                // Проверяем, заполнены ли оба поля
                if (login.value.isEmpty() || password.value.isEmpty()) {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                } else {

                    CoroutineScope(Dispatchers.IO).launch {
                        val user = userDao.getUser(login.value, password.value)
                        val userName = login.value
                        if (user != null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Вы успешно зашли в свой аккаунт",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("ExamsScreen")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Пользователь не найден",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00508A), // Цвет фона кнопки
                contentColor = Color.White // Цвет текста на кнопке
            )
        ) {
            Text(text = "SignIn")
        }
    }
}

@Composable
fun SignUpState(userDao: Dao, context: Context, navController: NavController, userName: String) {
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxHeight(0.99f)
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
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.skyBlue),
                unfocusedContainerColor = colorResource(id = R.color.skyBlue),
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black,
                focusedTextColor = Color.Black
            )
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
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.skyBlue),
                unfocusedContainerColor = colorResource(id = R.color.skyBlue),
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black,
                focusedTextColor = Color.Black
            )
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
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.skyBlue),
                unfocusedContainerColor = colorResource(id = R.color.skyBlue),
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black,
                focusedTextColor = Color.Black
            )
        )

        Button(
            onClick = {
                if (login.value.isEmpty() || password.value.isEmpty()) {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val newUser =
                        UsersEntity(
                            login = login.value,
                            password = password.value,
                            email = email.value
                        )
                    val user = userDao.getUser(login.value, password.value)

                    if (user == null) {
                        userDao.insertItem(newUser)
                        withContext(Dispatchers.Main) {
                            // Переход на новое активити или другие действия
                            withContext(Dispatchers.Main) {
                                navController.navigate("ExamsScreen")
                                Toast.makeText(
                                    context,
                                    "Вы успешно зашли в свой аккаунт",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {

                        Log.d("MyLog", "Такого аккаунта не существует")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Такой пользователь уже существует",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    userDao.insertItem(newUser)
                    // Переход на новое активити или другие действия
                }
            }
                },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00508A), // Цвет фона кнопки
                contentColor = Color.White // Цвет текста на кнопке
            )

            ) {
            Text(text = "SignUp")
        }
    }
}


enum class HomeTabs(

    val text: String
) {
    SignIn(
        text = "SignIn"
    ),
    SignUp(
        text = "SignUp"
    )
}