package com.example.lego.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lego.database.entity.Inventory

@Dao
interface InventoriesDAO {
    @Query("SELECT * FROM Inventories")
    fun getAll(): List<Inventory>

    @Query("SELECT EXISTS(SELECT * FROM Inventories WHERE Id = :id)")
    fun checkIfExistsById(id: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM Inventories WHERE Name = :name)")
    fun checkIfExistsByName(name: String): Boolean

    @Insert
    fun insert(vararg newInventory: Inventory)
}