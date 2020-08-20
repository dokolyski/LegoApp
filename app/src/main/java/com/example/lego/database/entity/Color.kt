package com.example.lego.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Colors")
data class Color(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "Code") val code: String?,
    @ColumnInfo(name = "Name") val name: String?,
    @ColumnInfo(name = "NamePL") val namePL: String?
)