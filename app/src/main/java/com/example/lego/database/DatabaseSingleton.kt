package com.example.lego.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lego.database.dao.InventoriesDAO
import com.example.lego.database.dao.ItemTypesDAO
import com.example.lego.database.entity.Inventory
import com.example.lego.database.entity.ItemType

@Database(entities = arrayOf(ItemType::class, Inventory::class), version = 1)
abstract class DatabaseSingleton : RoomDatabase() {
    abstract fun ItemTypesDAO (): ItemTypesDAO

    abstract fun InventoriesDAO (): InventoriesDAO

    companion object{
        var INSTANCE: DatabaseSingleton? = null
        fun getInstance(context: Context): DatabaseSingleton{
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    DatabaseSingleton::class.java,
                    "BrickList")
                    .build()
            }

            return INSTANCE as DatabaseSingleton
        }
    }
}