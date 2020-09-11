package com.example.lego.activity.inventoryActivity

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.InventoryPart

class RowDataService(
    private val position: Int,
    private val inventoriesLiveData: MutableLiveData<ItemAdapter.LayoutRowData>,
    private val context: Context,
    private val item: InventoryPart
) : Runnable {
    override fun run() {
        val databaseSingleton: DatabaseSingleton = DatabaseSingleton.getInstance(context)
        inventoriesLiveData.postValue(
            ItemAdapter.LayoutRowData(
            position,
            databaseSingleton.PartsDAO().findById(item.itemID)?.let {
                it.namePL ?: it.name
            } ?: "",
            databaseSingleton.ColorsDao().findByCode(item.colorId)?.let {
                "${it.namePL ?: it.name} [${item.itemID}]"
            } ?: "",
            // load image into imageView
            databaseSingleton.CodesDAO()
                .findByItemIdAndColorId(item.itemID, item.colorId)?.image?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                } ?: databaseSingleton.CodesDAO().findByItemId(item.itemID)?.image?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }
        ))
    }
}