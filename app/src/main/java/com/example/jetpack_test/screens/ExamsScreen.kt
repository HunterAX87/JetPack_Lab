package com.example.jetpack_test.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.jetpack_test.R
import com.example.jetpack_test.data.Exam
import com.example.jetpack_test.db.ExamDao
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ExamsScreen(examDao: ExamDao) {
    // Состояние для хранения списка экзаменов
    var exams by remember { mutableStateOf(listOf<Exam>()) }
    val scope = rememberCoroutineScope()

    // Получаем данные из базы данных в корутине при первом запуске экрана
    LaunchedEffect(Unit) {
        scope.launch {
            exams = examDao.getAllExams() // Получаем все экзамены из базы данных
        }
    }

    // Состояние для управления открытием диалогового окна
    var selectedExam by remember { mutableStateOf<Exam?>(null) }

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
                text = "Exams",
                color = colorResource(id = R.color.black),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp)) // Отступ между текстом и LazyRow

            // Отображаем LazyRow с экзаменами
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(exams) { exam ->
                    ExamCard(exam) {
                        // Устанавливаем выбранный экзамен и открываем диалоговое окно
                        selectedExam = exam
                    }
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

    // Если выбранный экзамен не null, показываем диалоговое окно
    selectedExam?.let { exam ->
        ShowExamDialog(exam) { selectedExam = null } // Передаем функцию закрытия диалога
    }
}

@Composable
fun ExamCard(exam: Exam, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.08f)
            .clickable(onClick = onClick), // Добавляем обработчик нажатия
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.blue)) // Изменяем цвет фона карточки
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = exam.subjectName,
                color = Color.White,
                fontSize = 18.sp)
        }
    }
}

@Composable
fun ShowExamDialog(exam: Exam, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Exam Details") },
        containerColor = colorResource(id = R.color.gray1),
        text = {
            Column {
                Text(text = "Subject Name: ${exam.subjectName}")
                Text(text = "Grade: ${exam.grade}")
                Text(text = "Group: ${exam.group}")
                Text(text = "Date: ${exam.examDate}")
                Text(text = "Time: ${exam.examTime}")
                Text(text = "Type: ${exam.examType}")
                Text(text = "Location: ${exam.location}")
                Text(text = "Professor: ${exam.professorName}")
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

