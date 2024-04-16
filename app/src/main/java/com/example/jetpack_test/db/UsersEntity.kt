package com.example.jetpack_test.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( tableName = "users_table")
data class UsersEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int?= null,
    var email: String,
    var login: String,
    var password: String
    )
