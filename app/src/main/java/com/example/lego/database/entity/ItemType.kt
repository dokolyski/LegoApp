package com.example.lego.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ItemTypes")
data class ItemType(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "Code") val code: Int?,
    @ColumnInfo(name = "Name") val name: String?,
    @ColumnInfo(name = "NamePL") val namePL: String?
)