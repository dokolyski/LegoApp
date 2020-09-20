package com.example.lego.activity.mainActivity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lego.R
import com.example.lego.activity.partsListActivity.PartsListActivity
import com.example.lego.activity.partsListActivity.ProjectAdapter
import com.example.lego.activity.settingsActivity.SettingsActivity
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.Inventory
import com.example.lego.xml.DownloadXmlTask
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val inventoriesLiveData: MutableLiveData<List<Inventory>> by lazy {
        MutableLiveData<List<Inventory>>()
    }
    private val labelsArray = arrayListOf<String>()

    override fun onResume() {
        super.onResume()
        Thread {
            val alsoArchived: Boolean = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean(resources.getString(R.string.show_archived), false)
            inventoriesLiveData.postValue(DatabaseSingleton.getInstance(this).InventoriesDAO().findAll(alsoArchived))
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addNewProjectButton = findViewById<Button>(R.id.downloadNewSetButton)
        addNewProjectButton.setOnClickListener {
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
            labelsArray.clear()
            inventoryList.forEach {
                labelsArray.add(it.name)
            }
            listView.adapter = ProjectAdapter(this)
        }

        inventoriesLiveData.observe(this, inventoriesObserver)

        listView.setOnItemClickListener { parent, view, position, id ->
            val element = listView.adapter.getItem(position)

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                val inventoryName = labelsArray[position]
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        // przejscie do widoku projektu
                        val intent = Intent(this, PartsListActivity::class.java)
                        intent.putExtra("inventoryName", inventoryName)
                        startActivity(intent)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    // Archiwizacja/dearchiwizacja projektu
                                    Thread {
                                        DatabaseSingleton.getInstance(this).InventoriesDAO().changeArchiveStatus(inventoryName)
                                        onResume()
                                    }.start()
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
