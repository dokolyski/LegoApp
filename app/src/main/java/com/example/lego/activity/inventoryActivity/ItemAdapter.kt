package com.example.lego.activity.inventoryActivity

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lego.R
import com.example.lego.database.entity.InventoryPart

class ItemAdapter(
    private val context: Context,
    private val items: List<InventoryPart>) : BaseAdapter( ){
    data class LayoutRowData(val position: Int, val title: String, val description: String, val imageBitmap: Bitmap?)

    private val inventoriesLiveData: Array<MutableLiveData<LayoutRowData>?> = arrayOfNulls(items.size)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val rowMain = layoutInflater.inflate(R.layout.row_item, parent, false)

        inventoriesLiveData[position] = MutableLiveData()

        val item: InventoryPart = items[position]

        val maxElements: TextView = rowMain.findViewById(R.id.textView_maxElements)
        val itemsNumberElement: TextView = rowMain.findViewById(R.id.integer_number)

        maxElements.text = item.quantityInSet.toString()
        itemsNumberElement.text = item.QuantityInStore.toString()

        // Create the observer which updates the UI.
        val inventoriesObserver = Observer<LayoutRowData> { rowData ->
            parent?.let {
//                val index: Int = parent.childCount - 1 - parent[parent.childCount - 1].id + position
                val listItem = parent[position] as ViewGroup
                listItem.forEach {
                    when(it.id) {
                        R.id.textView_top -> (it as TextView).text = rowData.title
                        R.id.textView_down -> (it as TextView).text = rowData.description
                        R.id.imageView -> (it as ImageView).setImageBitmap(rowData.imageBitmap)
                    }
                }
            }
        }

        inventoriesLiveData[position]?.let {
            it.observe(context as LifecycleOwner, inventoriesObserver)
            Thread(
                RowDataService(
                    position,
                    it,
                    context,
                    item
                )
            ).start()
        }

        val increase = rowMain.findViewById<Button>(R.id.increase)
        val decrease = rowMain.findViewById<Button>(R.id.decrease)

        increase.setOnClickListener {
            val listItem = it.parent as LinearLayout
            for (i: Int in 0 until listItem.childCount) {
                val child: View = listItem.get(i)
                if (child.id == R.id.integer_number) {
                    increaseInteger(child as TextView)
                }
            }
        }
        decrease.setOnClickListener {
            val listItem = it.parent as LinearLayout
            for (i: Int in 0 until listItem.childCount) {
                val child: View = listItem.get(i)
                if (child.id == R.id.integer_number) {
                    decreaseInteger(child as TextView)
                }
            }
        }

        return rowMain
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

}