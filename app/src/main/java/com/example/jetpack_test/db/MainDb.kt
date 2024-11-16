package com.example.jetpack_test.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jetpack_test.data.Event
import com.example.jetpack_test.data.Exam

@Database(entities = [UsersEntity::class, Exam::class, Event::class], version = 1, exportSchema = false)
abstract class MainDb : RoomDatabase() {
    abstract fun userDao(): Dao
    abstract fun examDao(): ExamDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: MainDb? = null

        fun getInstance(context: Context): MainDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDb::class.java,
                    "semestr.db" // Имя вашей базы данных
                )
                    .createFromAsset("semestr.db") // Загружаем базу данных из assets
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}