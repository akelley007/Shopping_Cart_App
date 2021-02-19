package com.hyperliteapps.pricechecker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
open class Item (
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: String,
    @ColumnInfo(name = "qrUrl") val qrUrl: String,
    @ColumnInfo(name = "thumbnail") val thumbnail: String
)