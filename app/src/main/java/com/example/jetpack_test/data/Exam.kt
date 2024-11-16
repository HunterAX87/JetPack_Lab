package com.example.jetpack_test.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) // Автоматическая генерация ID
    val id: Int = 0,

    @ColumnInfo(name = "grade") // Имя столбца в таблице
    val grade: Int,

    @ColumnInfo(name = "groupp") // Имя столбца в таблице
    val group: String,

    @ColumnInfo(name = "subject_name") // Имя столбца в таблице
    val subjectName: String,

    @ColumnInfo(name = "exam_date") // Имя столбца в таблице
    val examDate: String,

    @ColumnInfo(name = "exam_time") // Имя столбца в таблице
    val examTime: String,

    @ColumnInfo(name = "exam_type") // Имя столбца в таблице
    val examType: String,

    @ColumnInfo(name = "location") // Имя столбца в таблице
    val location: String,

    @ColumnInfo(name = "professor_name") // Имя столбца в таблице
    val professorName: String
)