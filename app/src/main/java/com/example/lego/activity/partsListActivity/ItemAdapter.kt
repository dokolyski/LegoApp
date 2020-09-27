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

class ItemAdapter(
    private val context: Context,
    private val items: List<LayoutRowData>,
) : BaseAdapter() {
    private val completedColor = Color.GREEN

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val rowView = layoutInflater.inflate(R.layout.row_item, parent, false)
        val item: LayoutRowData = items[position]

        fillLayout(rowView, item)

        val plusButton = rowView.findViewById<ImageButton>(R.id.plusButton)
        val minusButton = rowView.findViewById<ImageButton>(R.id.minusButton)

        plusButton.setOnClickListener {
            changePartsQuantity(1, it, position)
        }
        minusButton.setOnClickListener {
            changePartsQuantity(-1, it, position)
        }
        return rowView
    }

    override fun getItem(position: Int): Any {
        return items[position].quantityInStore
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    private fun fillLayout(rowView: View, data: LayoutRowData) {
        val inStoreToInSetCounter: TextView = rowView.findViewById(R.id.inStoreToInSetCounter)
        val mainLabel: TextView = rowView.findViewById(R.id.inventoryRowName)
        val descriptionLabel: TextView = rowView.findViewById(R.id.inventoryRowCode)
        val imageView: ImageView = rowView.findViewById(R.id.imageView)
        val parent: ConstraintLayout = rowView.findViewById(R.id.parent)

        inStoreToInSetCounter.text = formatCounter(data.quantityInStore, data.quantityInSet)
        mainLabel.text = data.title
        descriptionLabel.text = data.description
        imageView.setImageBitmap(data.imageBitmap)

        if( (data.quantityInSet == data.quantityInStore)) {
            parent.setBackgroundColor(completedColor)
        }
    }

    private fun changePartsQuantity(change: Int, clickedButton: View, position: Int) {
        val listItem = clickedButton.parent as ConstraintLayout
        for (i: Int in 0 until listItem.childCount) {
            val child: View = listItem[i]
            if (child.id == R.id.inStoreToInSetCounter) {
                val newQuantity: Int = items[position].quantityInStore + change
                if( 0 <= newQuantity && newQuantity <= items[position].quantityInSet) {
                    items[position].quantityInStore = newQuantity
                    (child as TextView).text = formatCounter(newQuantity, items[position].quantityInSet)
                    if ( newQuantity == items[position].quantityInSet)
                        listItem.setBackgroundColor(completedColor)
                    else
                        listItem.setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

    private fun formatCounter(inStore: Int, inSet: Int) = "$inStore / $inSet"
}