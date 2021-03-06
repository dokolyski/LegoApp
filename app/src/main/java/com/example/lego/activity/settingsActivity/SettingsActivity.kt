package com.example.lego.activity.settingsActivity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import com.example.lego.R
import com.example.lego.database.DatabaseSingleton

class SettingsActivity: AppCompatActivity() {
    private lateinit var pathPrefixOfURL: EditText
    private lateinit var showArchivedSwitch: Switch
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        pathPrefixOfURL = findViewById(R.id.pathPrefixOfUrlPlainText)
        showArchivedSwitch = findViewById(R.id.archivedSwitch)
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        setActualValues()

        val purgeButton = findViewById<Button>(R.id.purgeDatabaseButton)
        purgeButton.setOnClickListener {
            Thread {
                DatabaseSingleton.getInstance(this).InventoriesPartsDAO().deleteAll()
                DatabaseSingleton.getInstance(this).InventoriesDAO().deleteAll()
                DatabaseSingleton.getInstance(this).CodesDAO().deleteAllImages()
            }.start()
        }
    }

    override fun onPause() {
        saveChanges()
        super.onPause()
    }

    private fun setActualValues() {
        pathPrefixOfURL.setText(sharedPref.getString(resources.getString(R.string.source_url), resources.getString(R.string.defaultSourceUrl)))
        showArchivedSwitch.isChecked = sharedPref.getBoolean(resources.getString(R.string.show_archived), false)
    }

    private fun saveChanges() {
        sharedPref.edit()
            .putString(resources.getString(R.string.source_url), pathPrefixOfURL.text.toString())
            .putBoolean(resources.getString(R.string.show_archived), showArchivedSwitch.isChecked)
            .apply()
    }
}