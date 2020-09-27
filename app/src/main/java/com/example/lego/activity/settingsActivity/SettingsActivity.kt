package com.example.lego.activity.settingsActivity

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.lego.R
import com.example.lego.database.DatabaseSingleton

class SettingsActivity : AppCompatActivity() {
    private lateinit var pathPrefixOfURL: EditText
    private lateinit var showArchivedSwitch: Switch
    private lateinit var sharedPref: SharedPreferences
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        progressBar = findViewById(R.id.purgeDatabaseProgressBar)
        progressBar.isVisible = false
        pathPrefixOfURL = findViewById(R.id.pathPrefixOfUrlPlainText)
        showArchivedSwitch = findViewById(R.id.archivedSwitch)
        sharedPref =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        setActualValues()

        val purgeButton = findViewById<Button>(R.id.purgeDatabaseButton)
        purgeButton.setOnClickListener {
            onDatabasePurgeClicked()
        }
    }

    override fun onPause() {
        saveChanges()
        super.onPause()
    }

    private fun setActualValues() {
        pathPrefixOfURL.setText(sharedPref.getString(resources.getString(R.string.source_url),
            resources.getString(R.string.defaultSourceUrl)))
        showArchivedSwitch.isChecked =
            sharedPref.getBoolean(resources.getString(R.string.show_archived), false)
    }

    private fun saveChanges() {
        sharedPref.edit()
            .putString(resources.getString(R.string.source_url), pathPrefixOfURL.text.toString())
            .putBoolean(resources.getString(R.string.show_archived), showArchivedSwitch.isChecked)
            .apply()
    }

    private fun onDatabasePurgeClicked() {
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        progressBar.isVisible = true
                        Thread {
                            DatabaseSingleton.getInstance(this).InventoriesDAO().deleteAll()
                            DatabaseSingleton.getInstance(this).InventoriesPartsDAO()
                                .deleteAll()
                            DatabaseSingleton.getInstance(this).CodesDAO().deleteAllImages()
                            this.runOnUiThread {
                                progressBar.isVisible = false
                            }
                        }.start()
                    }
                }
            }
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.purge_message))
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }
}