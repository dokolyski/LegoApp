package com.example.lego.activity.partsListActivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import com.example.lego.R

class ItemAdapter(
    private val context: Context,
    private val items: List<LayoutRowData>,
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val rowView = layoutInflater.inflate(R.layout.row_item, parent, false)
        val item: LayoutRowData = items[position]

        fillLayout(rowView, item)

        val increase = rowView.findViewById<Button>(R.id.increase)
        val decrease = rowView.findViewById<Button>(R.id.decrease)
        val maxNumber = rowView.findViewById<TextView>(R.id.textView_maxElements)

        increase.setOnClickListener {
            val listItem = it.parent as LinearLayout
            for (i: Int in 0 until listItem.childCount) {
                val child: View = listItem[i]
                if (child.id == R.id.integer_number) {
                    if( (child as TextView).text.toString().toInt() != maxNumber.text.toString().toInt())
                        increaseInteger(child)
                }
            }
        }
        decrease.setOnClickListener {
            val listItem = it.parent as LinearLayout
            for (i: Int in 0 until listItem.childCount) {
                val child: View = listItem[i]
                if (child.id == R.id.integer_number) {
                    if( (child as TextView).text.toString().toInt() > 0)
                        decreaseInteger(child)
                }
            }
        }

        return rowView
    }

    override fun getItem(position: Int): Any {
        return "TEST"
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    fun increaseInteger(number: TextView) {
        display(number, number.text.toString().toInt() + 1)
    }

    fun decreaseInteger(number: TextView) {
        display(number, number.text.toString().toInt() - 1)
    }

    private fun display(number: TextView, newNumber: Int) {
        number.setText("$newNumber")
    }

    private fun fillLayout(rowView: View, data: LayoutRowData) {
        val maxElements: TextView = rowView.findViewById(R.id.textView_maxElements)
        val itemsNumberElement: TextView = rowView.findViewById(R.id.integer_number)
        val mainLabel: TextView = rowView.findViewById(R.id.textView_top)
        val descriptionLabel: TextView = rowView.findViewById(R.id.textView_down)
        val imageView: ImageView = rowView.findViewById(R.id.imageView)

        maxElements.text = data.quantityInSet
        itemsNumberElement.text = data.quantityInStore
        mainLabel.text = data.title
        descriptionLabel.text = data.description
        imageView.setImageBitmap(data.imageBitmap)
    }

}