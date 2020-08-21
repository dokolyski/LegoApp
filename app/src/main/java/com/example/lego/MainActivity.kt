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

        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.downloadNewSetButton)
        button.setOnClickListener {
            Log.d("EVENT", "button clicked")
            button.isEnabled = false
            DownloadXmlTask(this).execute()
        }
    }
}
