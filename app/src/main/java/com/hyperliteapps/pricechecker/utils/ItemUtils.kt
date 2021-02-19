package com.hyperliteapps.pricechecker.utils

import com.hyperliteapps.pricechecker.models.CartItem
import java.text.DecimalFormat

class ItemUtils {
    companion object {
        //utility item for getting price as Double
        fun getItemPrice(item: CartItem): Double {
            return item.item.price.substring(1).toDouble() * item.quantity
        }

        //utility method for formatting amount to currency
        fun formatToCurrency(amount: Double): String {
            val formatter = DecimalFormat("###,###,##0.00")
            return formatter.format(amount)
        }
    }
}