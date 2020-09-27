package com.example.lego.activity.mainActivity

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.lego.R
import com.example.lego.database.entity.Inventory
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class InventoryAdapter(
    private val context: Context,
    private val inventories: MutableList<Inventory>,
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val rowView = layoutInflater.inflate(R.layout.row_inventory, parent, false)

        val item = inventories[position]
        val titleTextView: TextView = rowView.findViewById(R.id.inventoryRowName)
        titleTextView.text = context.getString(R.string.partsListTitle, item.name, item.id.toString())
        if (item.active == 0) {
            titleTextView.setTextColor(Color.GRAY)
            titleTextView.setTypeface(null, Typeface.ITALIC)
        } else {
            titleTextView.setTextColor(Color.BLACK)
            titleTextView.setTypeface(null, Typeface.NORMAL)
        }

        val dateTime: LocalDateTime = LocalDateTime.ofEpochSecond(item.lastAccess.toLong(), 0, ZoneOffset.ofHours(2))
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm")
        TimeZone.getDefault().displayName
        rowView.findViewById<TextView>(R.id.inventoryRowLastAccessValue).text = dateTime.format(formatter)

        return rowView
    }

    override fun getItem(position: Int): Any {
        return inventories[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return inventories.size
    }
}