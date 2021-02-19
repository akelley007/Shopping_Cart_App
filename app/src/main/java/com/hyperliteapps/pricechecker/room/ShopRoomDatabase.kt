package com.hyperliteapps.pricechecker.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyperliteapps.pricechecker.models.Item
import com.hyperliteapps.pricechecker.utils.ShopUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ShopRoomDatabase: RoomDatabase() {


    abstract fun shoppingDao(): ShoppingDao

    private class ShopDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val gson = Gson()
            INSTANCE?.let { database ->
                scope.launch {
                    Log.v("ShopRoomDatabase", "populating database")
                    val shopDao = database.shoppingDao()

                    // Delete all content here.
                    shopDao.clear()

                    // Add preset list
                    val jsonArray = ShopUtils.getJsonFromAssets(context)
                    Log.v("ShopRoomDatabase", "$jsonArray")
                    val itemType = object : TypeToken<List<Item>>() {}.type
                    val itemList = gson.fromJson<List<Item>>(jsonArray, itemType)
                    itemList.forEach {
                        Log.v("ShopRoomDatabase", "adding ${it.name} to database")
                        shopDao.insert(it)
                    }
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ShopRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ShopRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                Log.v("ShopRoomDatabase", "creating database")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShopRoomDatabase::class.java,
                    "shop_database"
                )
                .addCallback(ShopDatabaseCallback(context, scope))
                .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}