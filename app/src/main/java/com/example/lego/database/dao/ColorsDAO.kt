package com.example.lego.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.lego.database.entity.Color

@Dao
interface ColorsDao {
    @Query("SELECT * FROM Colors")
    fun getAll(): List<Color>

    @Query("SELECT * FROM Colors WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Color>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Color
}