package com.example.lego.xml

import android.app.Application
import android.util.Xml
import androidx.lifecycle.AndroidViewModel
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.InventoryPart
import com.example.lego.xml.exceptions.PartNotFound
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.NullPointerException

data class Entry(var inventoryId: Int?, var typeId: Int?, var itemID: Int?, var quantityInSet: Int?, var quantityInStore: Int?, var colorID: Int?, var extra: Int?) {
    constructor() :
            this(null, null, null, null, null, null, null)

    @Throws(NullPointerException::class)
    fun castToInventoryPart(inventoryId: Int) : InventoryPart {
        return InventoryPart(0, inventoryId, typeId!!, itemID!!, quantityInSet!!, quantityInStore?:0, colorID?:0, extra?:0)
    }
}

// We don't use namespaces
private val ns: String? = null

class XMLParser(application: Application) : AndroidViewModel(application) {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): HashMap<String, List<*>> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): HashMap<String, List<*>> {
        val items = mutableListOf<Entry>()
        val itemsNotFound = mutableListOf<String>()

        parser.require(XmlPullParser.START_TAG, ns, "INVENTORY")
        while (parser.next() != XmlPullParser.END_TAG || parser.name != "INVENTORY") {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "ITEM") {
                try {
                    items.add(readItemProperties(parser))
                } catch (e: XmlPullParserException) {
                    continue
                } catch (e: PartNotFound) {
                    itemsNotFound.add(e.message!!)
                }
            }
        }
        return hashMapOf(
            "items" to items,
            "itemsNotFound" to itemsNotFound
        )
    }

    @Throws(XmlPullParserException::class, PartNotFound::class)
    private fun readItemProperties(parser: XmlPullParser): Entry {
        val part = Entry()
        var isAlternateN: Boolean = false
        parser.require(XmlPullParser.START_TAG, ns, "ITEM")
        while (parser.next() != XmlPullParser.END_TAG || parser.name != "ITEM") {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val tagName: String = parser.name

            if (parser.next() != XmlPullParser.TEXT) {
                continue
            }

            // TODO - sprawdzić czemu są duplikaty i niezgadzają się kolory (pierwsze miejsce)

            when(tagName) {
                "QTY" -> part.quantityInSet = parser.text.toInt()
                "QTYFILLED" -> part.quantityInStore = parser.text.toInt()
                "ITEMID" -> part.itemID = DatabaseSingleton.getInstance(getApplication()).PartsDAO().findIdByCode(parser.text)
                "ITEMTYPE" -> part.typeId = DatabaseSingleton.getInstance(getApplication()).ItemTypesDAO()
                    .findIdByItemType(parser.text)
                "COLOR" -> part.colorID = DatabaseSingleton.getInstance(getApplication()).ColorsDAO()
                    .findIdByCode(parser.text.toInt())
                "ALTERNATE" -> {
                    if (parser.text != "N") {
                        throw XmlPullParserException("ALTERNATE != N")
                    } else {
                        isAlternateN = true
                    }
                }
            }
        }
        if (!isAlternateN) throw XmlPullParserException("ALTERNATE != N")
        if (part.itemID == null) throw PartNotFound(PartNotFound.createMessage(part.itemID, part.colorID))
        return part
    }
}