package com.example.jetpack_test.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UsersEntity::class], version = 1)
abstract class MainDb: RoomDatabase() {
    abstract fun userDao(): Dao

    //companion object- для объявления переменных и функций, к которым требуется обращаться без создания экземпляра класса

}