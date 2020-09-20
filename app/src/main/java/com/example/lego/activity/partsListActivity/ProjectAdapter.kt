package com.example.lego.activity.partsListActivity

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import com.example.lego.R

class ProjectAdapter(
    private val context: Context
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val rowView = layoutInflater.inflate(R.layout.row_project, parent, false)

        //fillLayout(rowView, item)

        return rowView
    }

    override fun getItem(position: Int): Any {
        return "TEST"
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return 2
    }

    private fun fillLayout(rowView: View, data: LayoutRowData) {

    }

}