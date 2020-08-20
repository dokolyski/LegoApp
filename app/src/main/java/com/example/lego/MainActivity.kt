package com.example.lego

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.lego.xml.DownloadXmlTask

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getString(R.string.source_url), "http://fcds.cs.put.poznan.pl/MyWeb/BL/")
            commit()
        }

        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.downloadNewSetButton)
        button.setOnClickListener {
            Log.d("EVENT", "button clicked")
            button.isEnabled = false
            DownloadXmlTask(this).execute()
        }
    }
}
