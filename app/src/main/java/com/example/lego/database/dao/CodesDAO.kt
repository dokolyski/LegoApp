package com.example.lego.database.dao

import androidx.room.*
import com.example.lego.database.entity.Code

@Dao
interface CodesDAO {
    @Query("SELECT * FROM Codes WHERE ItemID = :itemId AND ColorID = :colorId LIMIT 1")
    fun findByItemIdAndColorId(itemId: Int, colorId: Int): Code?

    @Query("SELECT * FROM Codes")
    fun findAll(): List<Code>

    @Query("SELECT * FROM Codes WHERE id = :id")
    fun findById(id: Int): Code

    @Insert
    fun insertNewCode(parts: Code): Long

    @Update
    fun updateCode(parts: Code)

    @Update
    fun updateCodes(parts: List<Code>)

    fun deleteAllImages() = updateCodes(findAll().map { Code(it.id, it.itemId, it.colorId, it.code, null) })
}