package com.example.lego.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.lego.database.entity.Color

@Dao
interface ColorsDAO {
    @Query("SELECT * FROM Colors")
    fun getAll(): List<Color>

    @Query("SELECT id FROM Colors WHERE Code = :colorCode")
    fun findIdByCode(colorCode: Int): Int?

    @Query("SELECT * FROM Colors WHERE id = :id")
    fun findByCode(id: Int): Color?
}