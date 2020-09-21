package com.example.lego.database.dao

import androidx.room.*
import com.example.lego.database.entity.Inventory
import com.example.lego.database.entity.InventoryPart

@Dao
interface InventoriesPartsDAO {
    @Insert
    fun insertPart(part: InventoryPart)

    @Delete
    fun delete(parts: List<InventoryPart>)

    @Query("SELECT * FROM InventoriesParts")
    fun findAll(): List<InventoryPart>

    @Query("SELECT * FROM InventoriesParts WHERE InventoryID = :inventoryId")
    fun findAllByInventoryId(inventoryId: Int): List<InventoryPart>

    fun deleteAll() {
        delete(findAll())
    }

    @Update
    fun update(parts: List<InventoryPart>)
}