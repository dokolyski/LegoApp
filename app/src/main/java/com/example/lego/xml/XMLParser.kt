package com.example.lego.xml

import android.app.Application
import android.util.Xml
import androidx.lifecycle.AndroidViewModel
import com.example.lego.database.DatabaseSingleton
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

data class Entry(var inventoryId: Int?, var typeId: Int?, var itemID: Int?, var quantityInSet: Int?, var quantityInStore: Int?, var colorID: Int?, var extra: Int?) {
    constructor() :
            this(null, null, null, null, 0, null, null)
}

// We don't use namespaces
private val ns: String? = null

class XMLParser(application: Application) : AndroidViewModel(application) {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<Entry> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Entry> {
        val entries = mutableListOf<Entry>()

        parser.require(XmlPullParser.START_TAG, ns, "INVENTORY")
        while (parser.next() != XmlPullParser.END_TAG || parser.text != "INVENTORY") {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "ITEM") {
                try {
                    entries.add(readItemProperties(parser))
                } catch (e: XmlPullParserException) {
                    continue
                }
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readItemProperties(parser: XmlPullParser): Entry {
        val part = Entry()
        var isAlternateN: Boolean = false
        parser.require(XmlPullParser.START_TAG, ns, "ITEM")
        while (parser.next() != XmlPullParser.END_TAG || parser.text != "ITEM"){
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val tagName: String = parser.name

            if (parser.next() != XmlPullParser.TEXT) {
                continue
            }

            when(tagName) {
                "QTY" -> part.quantityInSet = parser.text.toInt()
                "QTYFILLED" -> part.quantityInStore = parser.text.toInt()
                "ITEMID" -> part.itemID = parser.text.toInt()
                "ITEMTYPE" -> part.typeId = DatabaseSingleton.getInstance(getApplication()).ItemTypesDAO()
                    .findByItemType(parser.text[0].toInt()).id
                // todo check this query
                "COLOR" -> part.colorID = DatabaseSingleton.getInstance(getApplication()).ItemTypesDAO()
                    .findByItemType(parser.text[0].toInt()).id
                "ALTERNATE" -> {
                    if (parser.text != "N") {
                        throw XmlPullParserException("ALTERNATE != N")
                    } else {
                        isAlternateN = true
                    }
                }
            }
        }
        if (parser.text != "ITEM") throw XmlPullParserException("UNEXPECTED TOKEN " + parser.text)
        if (!isAlternateN) throw XmlPullParserException("ALTERNATE != N")
        return part
    }
}