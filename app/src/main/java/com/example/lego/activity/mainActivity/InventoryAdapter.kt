package com.example.lego.activity.mainActivity

import android.content.Context
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
    private val inventories: List<Inventory>,
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val rowView = layoutInflater.inflate(R.layout.row_inventory, parent, false)

        val item = inventories[position]
        rowView.findViewById<TextView>(R.id.inventoryRowName).text = "${item.name} (${item.id})"

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