package com.example.jetpack_test.db
import androidx.room.Dao
import androidx.room.Query
import com.example.jetpack_test.data.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<Event>
}