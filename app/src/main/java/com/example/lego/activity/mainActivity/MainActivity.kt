package com.example.lego.activity.mainActivity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lego.activity.inventoryActivity.AboutProjectActivity
import com.example.lego.R
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.Inventory
import com.example.lego.xml.DownloadXmlTask

class MainActivity : AppCompatActivity() {
    private val inventoriesLiveData: MutableLiveData<List<Inventory>> by lazy {
        MutableLiveData<List<Inventory>>()
    }
    private val labelsArray = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.downloadNewSetButton)
        button.setOnClickListener {
            Log.d("EVENT", "button clicked")
            button.isEnabled = false
            DownloadXmlTask(this).execute()
        }

        val listView = findViewById<ListView>(R.id.partsListView)

        // Create the observer which updates the UI.
        val inventoriesObserver = Observer<List<Inventory>> { inventoryList ->
            // Update the UI, in this case, a TextView.
            labelsArray.clear()
            inventoryList.forEach {
                labelsArray.add(it.name)
            }
            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, labelsArray)
        }

        inventoriesLiveData.observe(this, inventoriesObserver)

        Thread {
            inventoriesLiveData.postValue(DatabaseSingleton.getInstance(this).InventoriesDAO().findAll())
        }.start()

        listView.setOnItemClickListener { parent, view, position, id ->
            val element = listView.adapter.getItem(position)

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        // przejscie do widoku projektu
                        val intent = Intent(this, AboutProjectActivity::class.java)
                        val inventoryName = labelsArray[position]
                        intent.putExtra("inventoryName", inventoryName)
                        startActivity(intent)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    // Archiwizacja projektu
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {

                                }
                            }
                        }

                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("Are you sure you want to archive this project")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            if (element != null) {
                builder.setMessage(element.toString())
                    .setPositiveButton("OPEN", dialogClickListener)
                    .setNegativeButton("ARCHIVE", dialogClickListener).show()
            }
        }
    }
}
