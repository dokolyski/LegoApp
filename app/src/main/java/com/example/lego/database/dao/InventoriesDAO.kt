package com.example.lego.database.dao

import androidx.room.*
import com.example.lego.database.entity.Inventory
import java.time.Instant

@Dao
interface InventoriesDAO {
    @Query("SELECT * FROM Inventories WHERE Name = :name")
    fun getByName(name: String): Inventory

    @Query("SELECT * FROM Inventories WHERE :alsoArchived OR Active")
    fun findAll(alsoArchived: Boolean = true): List<Inventory>

    @Query("SELECT EXISTS(SELECT * FROM Inventories WHERE Id = :id)")
    fun checkIfExistsById(id: Int): Boolean

    @Query("SELECT EXISTS(SELECT * FROM Inventories WHERE Name = :name)")
    fun checkIfExistsByName(name: String): Boolean

    @Insert
    fun insert(newInventory: Inventory)

    @Delete
    fun delete(inventories: List<Inventory>)

    @Query("SELECT Id FROM Inventories WHERE Name = :name LIMIT 1")
    fun findIdByName(name: String): Int?


    @Update
    fun update(inventory: Inventory)

    fun deleteAll() {
        delete(findAll())
    }

    fun updateLastAccessTime(inventoryName: String) {
        update(getByName(inventoryName).let {
            Inventory(it.id, it.name, it.active, Instant.now().epochSecond.toInt())
        })
    }

    fun changeArchiveStatus(inventoryName: String) {
        update(getByName(inventoryName).let {
            Inventory(it.id, it.name, if (it.active == 1) 0 else 1, it.lastAccess)
        })
    }
}