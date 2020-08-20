package com.example.lego.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Inventories")
data class Inventory(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "Name") val name: String?,
    @ColumnInfo(name = "Active") val active: Int?,
    @ColumnInfo(name = "LastAccessed") val lastAccess: Int?
)