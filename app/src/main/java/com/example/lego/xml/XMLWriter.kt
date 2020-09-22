package com.example.lego.xml

import android.content.Context
import com.example.lego.R
import com.example.lego.database.entity.InventoryPart
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.lang.Exception
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XMLWriter {
    companion object {
        fun writeXML(inventoryNo: Int, partsList: List<InventoryPart>, context: Context) {
            val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc: Document = docBuilder.newDocument()

            // Create the root node
            val rootElement: Element =  doc.createElement("INVENTORY")

            partsList.forEach {
                val missingPartsNumber = it.quantityInSet - it.QuantityInStore
                if (missingPartsNumber > 0) {
                    val itemElement: Element =  doc.createElement("ITEM")
                    itemElement.setAttribute("ITEMTYPE", "P")
                    itemElement.setAttribute("ITEMID", it.itemID.toString())
                    itemElement.setAttribute("COLOR", it.colorId.toString())
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
            context.getExternalFilesDir(null)?.let { dir ->
                val file = File(dir.path, "$inventoryNo.xml")
                transformer.transform(DOMSource(doc), StreamResult(file))
            } ?: throw Exception(R.string.sd_not_found.toString())

            // todo - show error message instead of throwing error
        }
    }
}