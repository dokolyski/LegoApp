package com.example.lego.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PartsDAO {
    @Query("SELECT id FROM Parts WHERE Code = :code LIMIT 1")
    fun findIdByCode(code: String): Int?

    @Query("SELECT Code FROM Parts WHERE id = :id LIMIT 1")
    fun findCodeById(id: Int): String?
}