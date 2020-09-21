package com.example.lego.activity.mainActivity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lego.R
import com.example.lego.activity.partsListActivity.PartsListActivity
import com.example.lego.activity.settingsActivity.SettingsActivity
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.Inventory
import com.example.lego.xml.DownloadXmlTask
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val inventoriesLiveData: MutableLiveData<List<Inventory>> by lazy {
        MutableLiveData<List<Inventory>>()
    }
    private var inventoriesMutableList = mutableListOf<Inventory>()

    override fun onResume() {
        super.onResume()
        loadInventoriesList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addNewProjectButton = findViewById<Button>(R.id.downloadNewSetButton)
        addNewProjectButton.setOnClickListener {
            // TODO - kółko zaczyna się kręcić, cały layout jest disabled
            addNewProjectButton.isEnabled = false
            DownloadXmlTask(this).execute()
        }

        val goToSettingsButton = findViewById<FloatingActionButton>(R.id.settingsButton)
        goToSettingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val listView = findViewById<ListView>(R.id.partsListView)

        // Create the observer which updates the UI.
        val inventoriesObserver = Observer<List<Inventory>> { inventoryList ->
            // Update the UI, in this case, a TextView.
            inventoriesMutableList = inventoryList.toMutableList()
            inventoriesMutableList.sortByDescending { it.lastAccess }
            listView.adapter = InventoryAdapter(this, inventoriesMutableList)
        }

        inventoriesLiveData.observe(this, inventoriesObserver)

        listView.setOnItemClickListener { parent, view, position, id ->
            val element = listView.adapter.getItem(position) as Inventory

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        // przejscie do widoku projektu
                        val intent = Intent(this, PartsListActivity::class.java)
                        intent.putExtra("inventoryName", element.name)
                        startActivity(intent)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    // Archiwizacja/dearchiwizacja projektu
                                    Thread {
                                        DatabaseSingleton.getInstance(this).InventoriesDAO().changeArchiveStatus(element.name)
                                        loadInventoriesList()
                                    }.start()
                                }
                            }
                        }

                        val builder = AlertDialog.Builder(this)
                        builder.setMessage( if (element.active == 1) getString(R.string.archive_message) else getString(R.string.dearchive_message))
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setMessage(element.name)
                .setPositiveButton("OPEN", dialogClickListener)
                .setNegativeButton("ARCHIVE", dialogClickListener).show()
        }
    }

    fun loadInventoriesList() {
        Thread {
            val alsoArchived: Boolean = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean(resources.getString(R.string.show_archived), false)
            inventoriesLiveData.postValue(DatabaseSingleton.getInstance(this).InventoriesDAO().findAll(alsoArchived))
        }.start()
    }
}
