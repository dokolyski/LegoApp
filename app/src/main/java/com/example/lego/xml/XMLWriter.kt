package com.example.lego.xml

import android.app.Activity
import android.app.AlertDialog
import com.example.lego.R
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.InventoryPart
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XMLWriter {
    companion object {
        fun writeXML(inventoryNo: Int, partsList: List<InventoryPart>, context: Activity) {
            val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc: Document = docBuilder.newDocument()
            val databaseSingleton = DatabaseSingleton.getInstance(context)

            // Create the root node
            val rootElement: Element =  doc.createElement("INVENTORY")

            partsList.forEach {
                val missingPartsNumber = it.quantityInSet - it.QuantityInStore
                if (missingPartsNumber > 0) {
                    val itemElement: Element =  doc.createElement("ITEM")
                    itemElement.setAttribute("ITEMTYPE", "P")
                    itemElement.setAttribute("ITEMID", databaseSingleton.PartsDAO().findCodeById(it.itemID).toString())
                    itemElement.setAttribute("COLOR", databaseSingleton.ColorsDAO().findCodeById(it.colorId).toString())
                    itemElement.setAttribute("QTYFILLED", missingPartsNumber.toString())
                    rootElement.appendChild(itemElement)
                }
            }
            doc.appendChild(rootElement)

            val transformer: Transformer = TransformerFactory.newInstance().newTransformer()

            // ==== Start: Pretty print
            // https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            // ==== End: Pretty print

//            context.getExternalFilesDirs()
            val message = context.getExternalFilesDir(null)?.let { dir ->
                val file = File(dir.path, "$inventoryNo.xml")
                transformer.transform(DOMSource(doc), StreamResult(file))
                context.getString(R.string.export_OK_with_path) + file.path
            } ?: context.getString(R.string.export_ERROR)
            context.runOnUiThread {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(message).setPositiveButton("OK", null ).show()
            }
        }
    }
}