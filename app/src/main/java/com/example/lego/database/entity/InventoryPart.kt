package com.example.lego.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "InventoriesParts")
data class InventoryPart(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "InventoryID") val inventoryID: Int?,
    @ColumnInfo(name = "TypeID") val typeID: Int?,
    @ColumnInfo(name = "ItemID") val itemID: Int?,
    @ColumnInfo(name = "QuantityInSet") val quantityInSet: Int?,
    @ColumnInfo(name = "QuantityInStore") val QuantityInStore: Int?,
    @ColumnInfo(name = "ColorID") val colorId: Int?,
    @ColumnInfo(name = "Extra") val extra: Int?

)