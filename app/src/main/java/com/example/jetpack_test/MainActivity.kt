package com.example.jetpack_test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.jetpack_test.db.MainDb
import androidx.room.Room
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat.startActivity
import com.example.SecondActivity
import com.example.jetpack_test.db.Dao
import com.example.jetpack_test.db.UsersEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            this,
            MainDb::class.java,
            "test.db"
        ).build()
        val userDao = db.userDao()


        setContent {

            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "user_img",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .fillMaxHeight(0.3f)
                )

                mainScreen(userDao, this@MainActivity)


            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun mainScreen(userDao: Dao, context: Context) {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    //val user = userDao.getUser(login.value, password.value)


    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
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
                    0 -> SignInScreen(userDao, context)
                    1 -> SignUpScreen(userDao, context)
                }
            }
        }
    }
}


@Composable
fun SignInScreen(userDao: Dao, context: Context) {
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
                            openActivity(context)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Пользователь не найден", Toast.LENGTH_SHORT).show()
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
fun SignUpScreen(userDao: Dao, context: Context) {
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
                        openActivity(context)
                        // Переход на новое активити или другие действия
                        withContext(Dispatchers.Main) {

                            Toast.makeText(
                                context,
                                "Вы успешно зашли в свой аккаунт",
                                Toast.LENGTH_SHORT
                            ).show()
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


fun openActivity(context: Context) {
    val intent = Intent(context, SecondActivity::class.java)
    context.startActivity(intent)
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

