package com.example.jetpack_test.db
import androidx.room.Dao
import androidx.room.Query
import com.example.jetpack_test.data.Exam

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams")
    suspend fun getAllExams(): List<Exam>
}