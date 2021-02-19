package com.hyperliteapps.pricechecker.utils

import android.content.Context
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class ShopUtils {
    companion object {
        const val JSON_FILE_NAME = "shop_data.json"

        //util method for retrieving locally stored json data for database
        fun getJsonFromAssets(context: Context): String? {
            val jsonString: String
            jsonString = try {
                val inputStream: InputStream = context.assets.open(JSON_FILE_NAME)
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charset.defaultCharset())
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return jsonString
        }
    }
}