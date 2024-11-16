package com.example.jetpack_test.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpack_test.db.MainDb
import androidx.room.Room
import com.example.jetpack_test.R

class MainActivity : ComponentActivity() {
    val userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = MainDb.getInstance(this)
        val userDao = db.userDao()
        val examDao = db.examDao()
        val eventDao = db.eventDao()


        setContent {
            // Создание NavHost с использованием NavController и начального маршрута
            val navController = rememberNavController()
            var selectedIndex by remember { mutableStateOf(0) } // Состояние для отслеживания выбранного текста
            var showBottomBar by remember { mutableStateOf(true) }

            Scaffold(
                bottomBar = {
                    // Условно отображаем BottomAppBar только если не находимся на SignInUpScreen
                    if (showBottomBar) {
                        BottomAppBar(
                            containerColor = colorResource(id = R.color.gray1) // Изменяем фон BottomAppBar
                        ) {
                            Spacer(modifier = Modifier.weight(1f)) // Заполнитель для выравнивания

                            Text(
                                text = "Exams",
                                color = if (selectedIndex == 0) Color(0xFF00508A) else Color.Gray, // Цвет текста в зависимости от состояния
                                modifier = Modifier
                                    .weight(1f) // Занимает половину пространства
                                    .clickable {
                                        selectedIndex = 0
                                        navController.navigate("ExamsScreen")
                                    }
                                    .padding(16.dp), // Добавляем отступы для удобства клика
                                textAlign = TextAlign.Center // Центрируем текст
                            )

                            Spacer(modifier = Modifier.weight(1f)) // Заполнитель для выравнивания

                            Text(
                                text = "Events",
                                color = if (selectedIndex == 1) Color(0xFF00508A) else Color.Gray, // Цвет текста в зависимости от состояния
                                modifier = Modifier
                                    .weight(1f) // Занимает половину пространства
                                    .clickable {
                                        selectedIndex = 1
                                        navController.navigate("EventsScreen")
                                    }
                                    .padding(16.dp), // Добавляем отступы для удобства клика
                                textAlign = TextAlign.Center // Центрируем текст
                            )

                            Spacer(modifier = Modifier.weight(1f)) // Заполнитель для выравнивания
                        }
                    }
                }

            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "SignInUpScreen",
                    Modifier.padding(innerPadding)
                ) {
                    composable("SignInUpScreen") {
                        // Устанавливаем состояние видимости BottomAppBar в false при открытии этого экрана
                        showBottomBar = false
                        SignInUpScreen(
                            userDao,
                            this@MainActivity,
                            navController = navController,
                            userName = userName
                        )
                    }
                    composable("ExamsScreen") {
                        showBottomBar = true
                        ExamsScreen(examDao)
                    }
                    composable("EventsScreen") {
                        showBottomBar = true
                        EventsScreen(eventDao)
                    }
                }
            }
        }
    }
}

