package com.example.lego.activity.partsListActivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.InventoryPart

class LayoutRowData(
    context: Context,
    item: InventoryPart,
) {
    var id: Int
    var title: String
    var description: String
    var imageBitmap: Bitmap?
    var quantityInStore: Int
    var quantityInSet: Int

    init {
        val databaseSingleton: DatabaseSingleton = DatabaseSingleton.getInstance(context)
        id = item.id
        title = databaseSingleton.PartsDAO().findById(item.itemID)?.let {
            it.namePL ?: it.name
        } ?: ""
        description = databaseSingleton.ColorsDAO().findByCode(item.colorId)?.let {
            "${it.namePL ?: it.name} [${item.itemID}]"
        } ?: ""
        // load image into imageView
        imageBitmap = databaseSingleton.CodesDAO()
            .findByItemIdAndColorId(item.itemID, item.colorId)?.image?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }
        quantityInStore = item.QuantityInStore
        quantityInSet = item.quantityInSet
    }
}