package com.example.lego.activity.settingsActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lego.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val showArchivedSwitch = findViewById<Switch>(R.id.archivedSwitch)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val pathPrefixOfURL = findViewById<EditText>(R.id.pathPrefixOfUrlPlainText)
        val settingsView = findViewById<ConstraintLayout>(R.id.settingsView)

//        settingsView.setOnClickListener{
//            pathPrefixOfURL.is
//        }
//
//        pathPrefixOfURL.setOnClickListener {
//            //pathPrefixOfURL.isEnabled = true
//        }



    }

}