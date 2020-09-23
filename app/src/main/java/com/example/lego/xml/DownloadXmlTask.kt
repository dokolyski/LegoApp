package com.example.lego.xml

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.example.lego.R
import com.example.lego.activity.mainActivity.MainActivity
import com.example.lego.activity.partsListActivity.PartsListActivity
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

class DownloadXmlTask(private val activity: MainActivity) : AsyncTask<String, Void, String>() {
    val inputId: String =
        activity.findViewById<EditText>(R.id.blockSetIdInput).editableText.toString()
    val inputName: String =
        activity.findViewById<EditText>(R.id.blockSetNameInput).editableText.toString()
    var broken: Boolean = false

    override fun doInBackground(vararg urls: String): String {
        val sharedPref =
            activity.getSharedPreferences(activity.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE)
        val sourceUrl = sharedPref.getString(
            activity.resources.getString(R.string.source_url),
            activity.resources.getString(R.string.defaultSourceUrl)
        )

        inputId.toIntOrNull()?.run {
            if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO()
                    .checkIfExistsById(this)
            ) {
                broken = true
                return activity.application.resources.getString(R.string.inventory_id_repeated)
            }
        } ?: run {
            broken = true
            return activity.application.resources.getString(R.string.inventory_id_is_not_number)
        }
        if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO()
                .checkIfExistsByName(inputName)
        ) {
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

        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(activity, PartsListActivity::class.java)
            intent.putExtra("inventoryName", inputName)
            activity.startActivity(intent)
        }

        if (broken) {
            // show error
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(result).setPositiveButton("OK", null)
                .show()
        } else {
            // show view with decision what to do next
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(result)
                .setPositiveButton("Go to project", dialogClickListener)
                .setNegativeButton("Bact to menu", null).show()
        }
//        TODO - layout jest z powrotem klikalny, można wtedy usunąć isEnabled dla samego przycisku (m.in. poniżej)
        val progressBar: ProgressBar = activity.findViewById(R.id.projectLoadProgressBar)
        progressBar.isVisible = false

        activity.findViewById<Button>(R.id.downloadNewSetButton).isEnabled = true
        activity.loadInventoriesList()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(
        urlString: String,
        inventoryId: String,
        inventoryName: String,
    ): String {
        val parts: HashMap<String, List<*>>? =
            downloadFromUrl(urlString)?.use(XMLParser(activity.application)::parse)
        if (parts != null) {
            // TODO - sprawdzić czemu są duplikaty i niezgadzają się kolory (drugie miejsce)
            DatabaseSingleton.getInstance(activity.applicationContext).InventoriesDAO().insert(
                Inventory(
                    inventoryId.toInt(),
                    if (inventoryName == "") inventoryId else inventoryName,
                    lastAccess = Instant.now().epochSecond.toInt()
                )
            )
            val notFoundParts = ArrayList<String>(parts["itemsNotFound"] as List<String>)
            (parts["items"] as List<Entry>).forEach {
                try {
                    val newInventoryPart: InventoryPart =
                        it.castToInventoryPart(inventoryId.toInt())
                    saveImage(newInventoryPart, activity.applicationContext)
                    DatabaseSingleton.getInstance(activity.applicationContext).InventoriesPartsDAO()
                        .insertPart(newInventoryPart)
                } catch (e: NullPointerException) {
                    notFoundParts.add(PartNotFound.createMessage(it.itemID, it.colorID))
                }
            }
            return concatMessageWithInfoAboutNotFoundParts(activity.application.resources.getString(
                R.string.xml_success), notFoundParts)
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
        // code jest rekordem w tabeli Code gdzie trzymamy obrazki
        val databaseSingleton = DatabaseSingleton.getInstance(context)
        val partCode = databaseSingleton.PartsDAO().findCodeById(part.itemID)
        val colorCode = databaseSingleton.ColorsDAO().findCodeById(part.colorId)
        databaseSingleton.CodesDAO().findByItemIdAndColorId(
            part.itemID,
            part.colorId
        )?.let { code: Code ->
            if (partCode != null) {
                val imageURL =
                    if (colorCode != null) "https://www.bricklink.com/P/$colorCode/$partCode.jpg"
                    else "https://www.bricklink.com/PL/$partCode.jpg"
                if (code.image == null) {
                    try {
                        code.image = downloadImage(imageURL)
                        DatabaseSingleton.getInstance(context).CodesDAO().updateCode(code)
                    } catch (e: IOException) {
                        //image not found
                    }
                }
            }
        } ?: run {
            if (partCode != null) {
                val imageURL =
                    if (colorCode != null) "https://www.bricklink.com/P/$colorCode/$partCode.jpg"
                    else "https://www.bricklink.com/PL/$partCode.jpg"
                var image: ByteArray? = null
                try {
                    image = downloadImage(imageURL)
                } catch (e: IOException) {
                    //image not found
                }
                DatabaseSingleton.getInstance(context).CodesDAO()
                    .insertNewCode(Code(0, part.itemID, part.colorId, null, image))
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadImage(url: String): ByteArray {
        downloadFromUrl(url)?.use {
            return it.readBytes()
        }
        throw IOException("Cannot found image for this part")
    }

    private fun concatMessageWithInfoAboutNotFoundParts(
        message: String,
        notFoundParts: List<String>,
    ): String {
        message.plus("\n")
        notFoundParts.forEach { message.plus("\n" + it) }
        return message
    }
}