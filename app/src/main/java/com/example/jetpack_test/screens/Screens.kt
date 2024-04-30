package com.example.jetpack_test.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.jetpack_test.R
import com.example.jetpack_test.data.WeatherModel
import com.example.jetpack_test.db.Dao
import com.example.jetpack_test.db.UsersEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignInUpScreen(userDao: Dao, context: Context, navController: NavController) {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    //val user = userDao.getUser(login.value, password.value)


    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "user_img",
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .fillMaxHeight(0.3f)
            )

            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeTabs.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        selectedContentColor = Color.Green,
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
                    0 -> SignInState(userDao, context, navController)
                    1 -> SignUpState(userDao, context, navController)
                }
            }
        }
    }
}


@Composable
fun SignInState(userDao: Dao, context: Context, navController: NavController) {
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
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val user = userDao.getUser(login.value, password.value)
                    if (user != null) {

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Вы успешно зашли в свой аккаунт",
                                Toast.LENGTH_SHORT
                            ).show()
                            withContext(Dispatchers.Main) {
                                navController.navigate("WeatherScreen")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Пользователь не найден", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),

            ) {
            Text(text = "SignIn")
        }
    }
}

@Composable
fun SignUpState(userDao: Dao, context: Context, navController: NavController) {
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
            onClick = {
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
                                navController.navigate("WeatherScreen")
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
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),

            ) {
            Text(text = "SignUp")
        }
    }
}


@Composable
fun WeatherScreen(
    currentDay: MutableState<WeatherModel>,
    onClickSync: () -> Unit,
    onClickSearch: () -> Unit
) {

    Image(
        painter = painterResource(id = R.drawable.weather),
        contentDescription = "im1",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = currentDay.value.time,
                style = TextStyle(fontSize = 15.sp),
                color = Color.White
            )
        }

        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = currentDay.value.city,
            style = TextStyle(fontSize = 25.sp),
            color = Color.White
        )
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = currentDay.value.currentTemp.toFloat().toInt().toString() + "°C",
            style = TextStyle(fontSize = 75.sp),
            color = Color.White
        )
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = currentDay.value.condition,
            style = TextStyle(fontSize = 18.sp),
            color = Color.White
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    onClickSearch.invoke()
                }
            )
            {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "im3"
                )

            }

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = "${
                    currentDay.value.maxTemp.toFloat().toInt()
                }°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                style = TextStyle(fontSize = 15.sp),
                color = Color.White
            )

            IconButton(
                onClick = {
                    onClickSync.invoke()
                }
            )
            {
                Icon(
                    painter = painterResource(id = R.drawable.refresh_icon),
                    contentDescription = "im3"
                )
            }
        }

        Spacer(modifier = Modifier.height(70.dp))

        AsyncImage(
            model = "https:" + currentDay.value.icon,
            contentDescription = "im2",
            modifier = Modifier.size(150.dp)
        )
    }
}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit) {
    val dialogText = remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = {
            dialogState.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false
            }) {
                Text(text = "Ok")
            }
        },

        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text(text = "Cancel")
            }
        },

        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Введите название города")
                TextField(
                    value = dialogText.value,
                    onValueChange = {
                    dialogText.value= it
                },
                    singleLine = true)
            }
        }
    )
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
