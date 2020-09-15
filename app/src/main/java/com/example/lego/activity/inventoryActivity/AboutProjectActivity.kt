package com.example.lego.activity.inventoryActivity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lego.R
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.InventoryPart

class AboutProjectActivity : AppCompatActivity() {
    private val inventoriesPartsLiveData: MutableLiveData<List<LayoutRowData>> by lazy {
        MutableLiveData<List<LayoutRowData>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_project)

        val listView = findViewById<ListView>(R.id.partsListView)

        val title = findViewById<TextView>(R.id.textView_title)
        val inventoryName = intent.getStringExtra("inventoryName")
            ?: throw Throwable("InventoryName not passed")

        title.text = inventoryName

        val partsObserver = Observer<List<LayoutRowData>> {
            listView.adapter = ItemAdapter(this, it)
        }

        inventoriesPartsLiveData.observe(this, partsObserver)

        Thread {
            val databaseSingleton: DatabaseSingleton = DatabaseSingleton.getInstance(this)
            val codeInventory: Int? = databaseSingleton.InventoriesDAO().findIdByName(inventoryName)
            if (codeInventory != null) {
                val inventoryPartsList: List<InventoryPart> = databaseSingleton.InventoriesPartsDAO().findAllByInventoryId(codeInventory)
                inventoriesPartsLiveData.postValue(inventoryPartsList.map { LayoutRowData(this, it) })
            } else {
                throw Throwable("Inventory not found")
            }
        }.start()
    }
}
