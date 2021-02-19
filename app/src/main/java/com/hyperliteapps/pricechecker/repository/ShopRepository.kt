package com.hyperliteapps.pricechecker.repository

import androidx.annotation.WorkerThread
import com.hyperliteapps.pricechecker.models.Item
import com.hyperliteapps.pricechecker.room.ShoppingDao

class ShopRepository(private val dao: ShoppingDao) {

    fun getShoppingList() = dao.getShoppingItems()

    /**
     * extra methods for future implementation if required
     */
    @WorkerThread
    suspend fun insert(item: Item) {
        dao.insert(item)
    }

    @WorkerThread
    suspend fun remove(item: Item) {
        dao.remove(item)
    }

    @WorkerThread
    suspend fun clear() {
        dao.clear()
    }
}