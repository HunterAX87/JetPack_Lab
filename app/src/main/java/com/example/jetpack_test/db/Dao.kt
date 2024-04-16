package com.example.jetpack_test.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert
    fun insertItem(usersEntity:UsersEntity)

    @Delete
    fun deleteItem(usersEntity: UsersEntity)

    //Flow нужен для того чтоб, эта функция всегда обновлялась сама, когда есть обновления
    @Query("SELECT * FROM users_table")
    fun getAllItems():Flow<List<UsersEntity>>

    @Query("SELECT * FROM users_table WHERE login=:login AND password=:password")
    fun getUser(login:String, password:String):UsersEntity?
}