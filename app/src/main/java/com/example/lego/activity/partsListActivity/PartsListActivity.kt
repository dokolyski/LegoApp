package com.example.lego.activity.partsListActivity

import android.os.Bundle
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lego.R
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.InventoryPart
import com.example.lego.xml.XMLWriter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PartsListActivity : AppCompatActivity() {
    private val inventoriesPartsLiveData: MutableLiveData<List<LayoutRowData>> by lazy {
        MutableLiveData<List<LayoutRowData>>()
    }
    private lateinit var listView: ListView
    private lateinit var inventoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_project)

        listView = findViewById(R.id.partsListView)

        val progressBar: ProgressBar = findViewById(R.id.listLoadProgressBar)
        progressBar.isVisible = true

        val title = findViewById<TextView>(R.id.textView_title)
        inventoryName = intent.getStringExtra("inventoryName")
            ?: throw Throwable("InventoryName not passed")

        title.text = inventoryName

        val partsObserver = Observer<List<LayoutRowData>> {
            progressBar.isVisible = false
            listView.adapter = ItemAdapter(this, it)

            findViewById<FloatingActionButton>(R.id.exportMissingPartsButton).setOnClickListener {
                val partsListWithActualQuantityInStore = getIdToQuantityMap()
                Thread {
                    val inventoryId = DatabaseSingleton.getInstance(this).InventoriesDAO().findIdByName(inventoryName)
                    if (inventoryId != null) {
                        getPartsListWithActual(partsListWithActualQuantityInStore, false)?.let {partsList ->
                            XMLWriter.writeXML(inventoryId, partsList,this)
                        }
                    }
                }.start()
            }
        }

        inventoriesPartsLiveData.observe(this, partsObserver)

        Thread {
            val databaseSingleton: DatabaseSingleton = DatabaseSingleton.getInstance(this)
            databaseSingleton.InventoriesDAO().updateLastAccessTime(inventoryName)
            val codeInventory: Int? = databaseSingleton.InventoriesDAO().findIdByName(inventoryName)
            if (codeInventory != null) {
                val inventoryPartsList: List<InventoryPart> = databaseSingleton.InventoriesPartsDAO().findAllByInventoryId(
                    codeInventory)
                inventoriesPartsLiveData.postValue(inventoryPartsList.map {
                    LayoutRowData(this,
                        it)
                })
            } else {
                throw Throwable("Inventory not found")
            }
        }.start()
    }

    override fun onPause() {
        saveChanges()
        super.onPause()
    }

    private fun saveChanges() {
        val partsListWithActualQuantityInStore = getIdToQuantityMap()
        Thread {
            getPartsListWithActual(partsListWithActualQuantityInStore, true)
        }.start()
    }

    private fun getIdToQuantityMap(): MutableMap<Int, Int> {
        val listView = findViewById<ListView>(R.id.partsListView)
        val partsListWithActualQuantityInStore = mutableMapOf<Int, Int>()
        for (i in 0 until listView.adapter.count) {
            partsListWithActualQuantityInStore[listView.adapter.getItemId(i).toInt()] = listView.adapter.getItem(i) as Int
        }
        return partsListWithActualQuantityInStore
    }

    private fun getPartsListWithActual(partsListWithActualQuantityInStore: MutableMap<Int, Int>, withUpdate: Boolean): List<InventoryPart>? {
        val databaseSingleton: DatabaseSingleton = DatabaseSingleton.getInstance(this)
        return databaseSingleton.InventoriesDAO().findIdByName(inventoryName)?.let { inventoryId ->
            val codeInventory: List<InventoryPart> = databaseSingleton.InventoriesPartsDAO().findAllByInventoryId(inventoryId)
            val resultList = mutableListOf<InventoryPart>()
            codeInventory.forEach {
                resultList.add(InventoryPart(it.id, it.inventoryID, it.typeID, it.itemID,
                    it.quantityInSet, partsListWithActualQuantityInStore[it.id]!!, it.colorId, it.extra))
            }
            if (withUpdate) {
                databaseSingleton.InventoriesPartsDAO().update(resultList)
            }
            resultList
        }
    }
}
