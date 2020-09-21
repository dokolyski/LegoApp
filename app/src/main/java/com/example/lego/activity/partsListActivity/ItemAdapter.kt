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

        val increase = rowView.findViewById<Button>(R.id.increase)
        val decrease = rowView.findViewById<Button>(R.id.decrease)

        increase.setOnClickListener {
            changePartsQuantity(1, it, position)
        }
        decrease.setOnClickListener {
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
        val maxElements: TextView = rowView.findViewById(R.id.inventoryRowLastAccessLabel)
        val itemsNumberElement: TextView = rowView.findViewById(R.id.integer_number)
        val mainLabel: TextView = rowView.findViewById(R.id.inventoryRowName)
        val descriptionLabel: TextView = rowView.findViewById(R.id.inventoryRowCode)
        val imageView: ImageView = rowView.findViewById(R.id.imageView)

        maxElements.text = data.quantityInSet.toString()
        itemsNumberElement.text = data.quantityInStore.toString()
        mainLabel.text = data.title
        descriptionLabel.text = data.description
        imageView.setImageBitmap(data.imageBitmap)

        if( (data.quantityInSet == data.quantityInStore)){
            (maxElements.parent as ConstraintLayout).setBackgroundColor(completedColor)
        }
    }

    private fun changePartsQuantity(change: Int, clickedButton: View, position: Int) {
        val listItem = clickedButton.parent as LinearLayout
        for (i: Int in 0 until listItem.childCount) {
            val child: View = listItem[i]
            if (child.id == R.id.integer_number) {
                val newQuantity: Int = items[position].quantityInStore + change
                if( 0 <= newQuantity && newQuantity <= items[position].quantityInSet) {
                    items[position].quantityInStore = newQuantity
                    (child as TextView).text = "$newQuantity"
                    if ( newQuantity == items[position].quantityInSet)
                        (listItem.parent as ConstraintLayout).setBackgroundColor(completedColor)
                    else
                        (listItem.parent as ConstraintLayout).setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

}