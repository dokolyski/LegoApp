package com.example.lego.activity.mainActivity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.lego.R
import com.example.lego.xml.DownloadXmlTask


//class AddProject : AppCompatActivity(){
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_project)
//
//        val inputNumber = findViewById<EditText>(R.id.editTextNumber)
//        val inputProjectName = findViewById<EditText>(R.id.editTextName)
//        val addButton = findViewById<Button>(R.id.addButton)
//
//        inputNumber.addTextChangedListener(object : TextWatcher {
//
//            override fun afterTextChanged(s: Editable?) {
//
//                if(inputNumber.text.isNotEmpty()){
//                    addButton.isEnabled = true
//                }
//                else if(inputNumber.text.isEmpty()){
//                    addButton.isEnabled = false
//                }
//
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                return
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                return
//            }
//        })
//
//        addButton.setOnClickListener{
//
//            val dialogClickListener =
//                DialogInterface.OnClickListener { dialog, which ->
//                    when (which) {
//                        DialogInterface.BUTTON_POSITIVE -> {
//                            addButton.isEnabled = false
//                            DownloadXmlTask(this).execute()
//                            this.finish()
//                        }
//                        DialogInterface.BUTTON_NEGATIVE -> {
//
//                        }
//                    }
//                }
//
//            if( inputProjectName.text.isEmpty()) {
//                val builder = AlertDialog.Builder(this)
//                builder.setMessage("Are you sure you want to add project without name")
//                    .setPositiveButton("Yes", dialogClickListener)
//                    .setNegativeButton("No", dialogClickListener).show()
//            }
//            else{
//                addButton.isEnabled = false
////                DownloadXmlTask(this).execute()
//                this.finish()
//            }
//
//
//        }
//
//
//
//
//    }
//}