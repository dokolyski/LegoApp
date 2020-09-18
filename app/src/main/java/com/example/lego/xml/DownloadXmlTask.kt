package com.example.lego.xml

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.lego.R
import com.example.lego.database.DatabaseSingleton
import com.example.lego.database.entity.Code
import com.example.lego.database.entity.Inventory
import com.example.lego.database.entity.InventoryPart
import com.example.lego.xml.exceptions.PartNotFound
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import kotlin.jvm.Throws

class DownloadXmlTask(private val activity: Activity) : AsyncTask<String, Void, String>() {
    val inputId: String = activity.findViewById<EditText>(R.id.blockSetIdInput).editableText.toString()
    val inputName: String = activity.findViewById<EditText>(R.id.blockSetNameInput).editableText.toString()
    var broken: Boolean = false

    override fun doInBackground(vararg urls: String): String {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val sourceUrl = sharedPref.getString(
            activity.resources.getString(R.string.source_url),
            activity.resources.getString(R.string.defaultSourceUrl)
        )

        if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO().checkIfExistsById(
                inputId
            )) { // check is set already in database
            broken = true
            return activity.application.resources.getString(R.string.inventory_id_repeated)
        } else if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO().checkIfExistsByName(inputName)) {
            // check is set already in database
            broken = true
            return activity.application.resources.getString(R.string.inventory_name_repeated)
        }
        return try {
            loadXmlFromNetwork("$sourceUrl$inputId.xml", inputId, inputName)
        } catch (e: IOException) {
            broken = true
            activity.application.resources.getString(R.string.connection_error)
        } catch (e: XmlPullParserException) {
            broken = true
            activity.application.resources.getString(R.string.xml_error)
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (broken) {
            // TODO show error
        } else {
            // TODO show view with decision what to do next
        }
        activity.findViewById<Button>(R.id.downloadNewSetButton).isEnabled = true
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String, inventoryId: String, inventoryName: String) : String {
        val parts: HashMap<String, List<*>>? = downloadFromUrl(urlString)?.use(XMLParser(activity.application)::parse)
        if (parts != null) {
            DatabaseSingleton.getInstance(activity.applicationContext).InventoriesDAO().insert(
                Inventory(
                    inventoryId.toInt(),
                    if (inventoryName == "") inventoryId else inventoryName,
                    lastAccess = Instant.now().epochSecond.toInt()
                )
            )
            val notFoundParts = ArrayList<String>(parts["itemsNotFound"] as List<String>)
            (parts.get("items") as List<Entry>).forEach {
                try {
                    val newInventoryPart: InventoryPart = it.castToInventoryPart(inventoryId.toInt())
                    saveImage(newInventoryPart, activity.applicationContext)
                    DatabaseSingleton.getInstance(activity.applicationContext).InventoriesPartsDAO().insertPart(newInventoryPart)
                } catch (e: NullPointerException) {
                    notFoundParts.add(PartNotFound.createMessage(it.itemID, it.colorID))
                }
            }
            return concatMessageWithInfoAboutNotFoundParts(activity.application.resources.getString(R.string.xml_success), notFoundParts)
        }
        return activity.application.resources.getString(R.string.xml_error)
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    @Throws(IOException::class)
    private fun downloadFromUrl(urlString: String): InputStream? {
        val url = URL(urlString)
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // Starts the query
            connect()
            inputStream
        }
    }

    private fun saveImage(part: InventoryPart, context: Context) {
        val partCode = DatabaseSingleton.getInstance(context).PartsDAO().findCodeById(part.itemID)
        var code: Code? = DatabaseSingleton.getInstance(context).CodesDAO().findByItemIdAndColorId(
            part.itemID,
            part.colorId
        )
        if (code == null) {
            if (partCode != null) {
                val imageURL = "https://www.bricklink.com/PL/$partCode.jpg"
                code = DatabaseSingleton.getInstance(context).CodesDAO().findByItemId(part.itemID)
                if (code == null) {
                    var image: ByteArray? = null;
                    try {
                         image = downloadImage(imageURL)
                    } catch (e: IOException) {
                        //image not found
                    }
                    DatabaseSingleton.getInstance(context).CodesDAO()
                        .insertNewCode(Code(0, part.itemID, null, null, image)).toInt()
                    // id = 0 means id will be generated automatically
                } else {
                    try {
                        if (code.image == null) {
                            code.image = downloadImage(imageURL)
                        }
                        DatabaseSingleton.getInstance(context).CodesDAO().updateCode(code)
                    } catch (e: IOException) {
                        //image not found
                    }
                    code.id
                }
            }
        } else {
            val imageURL = "https://www.bricklink.com/P/${part.colorId}/$partCode.gif"
            if (code.image == null) {
                try {
                    code.image?:downloadImage(imageURL)
                    DatabaseSingleton.getInstance(context).CodesDAO().updateCode(code)
                } catch (e: IOException) {
                    //image not found
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadImage(url: String) : ByteArray {
        downloadFromUrl(url)?.use {
            return it.readBytes()
        }
        throw IOException("Cannot found image for this part")
    }

    private fun concatMessageWithInfoAboutNotFoundParts(message: String, notFoundParts: List<String>): String {
        message.plus("\n")
        notFoundParts.forEach { message.plus("\n" + it) }
        return message
    }
}