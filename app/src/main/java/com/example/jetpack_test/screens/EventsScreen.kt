package com.example.jetpack_test.screens

import com.example.jetpack_test.R
import com.example.jetpack_test.data.Event
import com.example.jetpack_test.db.EventDao
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun EventsScreen(eventDao: EventDao) {
    // Состояние для хранения списка событий
    var events by remember { mutableStateOf(listOf<Event>()) }
    val scope = rememberCoroutineScope()

    // Получаем данные из базы данных в корутине при первом запуске экрана
    LaunchedEffect(Unit) {
        scope.launch {
            events = eventDao.getAllEvents() // Получаем все события из базы данных
        }
    }

    // Состояние для управления открытием диалогового окна
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Используем Box для центрирования содержимого
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Центрируем содержимое
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(16.dp) // Добавляем отступы вокруг колонки
        ) {
            Text(
                text = "Events",
                color = colorResource(id = R.color.black),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp)) // Отступ между текстом и списком событий

            // Отображаем LazyColumn с событиями
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events) { event ->
                    EventCard(event) {
                        // Устанавливаем выбранное событие и открываем диалоговое окно
                        selectedEvent = event
                    }
                }

                // Добавляем текст в конце списка
                item {
                    Text(
                        text = "Tap to the card, to getting all information!",
                        color = colorResource(id = R.color.grray),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(18.dp) // Добавляем отступы вокруг текста
                    )
                }
            }

            Spacer(modifier = Modifier.height(23.dp))

            Text(
                text = "Tap to the card, to getting all information!",
                color = colorResource(id = R.color.grray),
                fontSize = 20.sp
            )
        }
    }

    // Если выбранное событие не null, показываем диалоговое окно
    selectedEvent?.let { event ->
        ShowEventDialog(event) { selectedEvent = null } // Передаем функцию закрытия диалога
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Добавляем обработчик нажатия на карточку
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.blue)) // Изменяем цвет фона карточки
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = event.name, color = Color.White, fontSize = 18.sp)
            Text(text = "Date: ${event.date}", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun ShowEventDialog(event: Event, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Event Details") },
        containerColor = colorResource(id = R.color.gray1),
        text = {
            Column {
                Text(text = "Name: ${event.name}")
                Text(text = "Date: ${event.date}")
                Text(text = "Location: ${event.location}")
                Text(text = "Organizer: ${event.organizer}")
                Text(text = "Participants Count: ${event.participantsCount}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00508A), // Цвет фона кнопки
                contentColor = Color.White // Цвет текста на кнопке
            )) {
                Text("Back")
            }
        }
    )
}