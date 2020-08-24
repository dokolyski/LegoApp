package com.example.lego.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Codes")
data class Code(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "ItemID") val itemId: Int,
    @ColumnInfo(name = "ColorID") val colorId: Int?,
    @ColumnInfo(name = "Code") val code: Int?,
    @ColumnInfo(name = "Image", typeAffinity = ColumnInfo.BLOB) var image: ByteArray?
)
