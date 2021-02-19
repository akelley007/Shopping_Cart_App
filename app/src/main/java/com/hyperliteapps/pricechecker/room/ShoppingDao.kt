package com.hyperliteapps.pricechecker.room

import androidx.room.*
import com.hyperliteapps.pricechecker.models.Item

@Dao
interface ShoppingDao {

    @Query("SELECT * FROM shopping_items ORDER BY id ASC")
    fun getShoppingItems(): List<Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun remove(item: Item)

    @Query("DELETE FROM shopping_items")
    suspend fun clear()
}