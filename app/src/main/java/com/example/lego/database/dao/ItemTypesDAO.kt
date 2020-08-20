package com.example.lego.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lego.database.entity.ItemType

@Dao
interface ItemTypesDAO {
    @Query("SELECT * FROM ItemTypes")
    fun getAll(): List<ItemType>

    @Query("SELECT * FROM ItemTypes WHERE Code = :itemType LIMIT 1")
    fun findByItemType(itemType: Int): ItemType
//
//    @Insert
//    fun insertAll(vararg users: List<ItemType>)
}