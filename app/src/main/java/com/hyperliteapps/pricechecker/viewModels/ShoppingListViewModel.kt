package com.hyperliteapps.pricechecker.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.hyperliteapps.pricechecker.models.ActionType
import com.hyperliteapps.pricechecker.models.CartItem
import com.hyperliteapps.pricechecker.models.Item
import com.hyperliteapps.pricechecker.repository.ShopRepository
import com.hyperliteapps.pricechecker.utils.ItemUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class ShoppingListViewModel(var repository: ShopRepository): ViewModel() {

    //live data for loading status
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    //live data for most recent cart action
    private val _cartAction = MutableLiveData<ActionType>()
    val cartAction: LiveData<ActionType>
        get() = _cartAction

    //live data for cart total
    private val _total = MutableLiveData<String>()
    val total: LiveData<String>
        get() = _total

    //live data for cart contents
    private val _cart = MutableLiveData<List<CartItem>>()
    val cart: LiveData<List<CartItem>>
        get() = _cart

    init {
        _cart.value = mutableListOf()
    }

    private lateinit var shoppingList: List<Item>
    private lateinit var lastRemovedItem: CartItem

    init {
        //start by getting the full list of items and prompting the database to be built
        getShoppingList()
    }

    private fun getShoppingList() {
        _loading.value = true

        //must be run on background thread as could block UI
        viewModelScope.launch(Dispatchers.IO) {
            shoppingList = repository.getShoppingList()
            withContext(Dispatchers.Main) {
                _loading.value = false
            }
        }
    }

    fun addToCart(id: String): Boolean {
        _loading.value = true

        //check to see if item attempting to be added exists in database
        //if yes, then add to cart, otherwise show error toast
        val storeItem = shoppingList.find { item -> item.id == id }
        return if (storeItem != null) {
            addToCart(storeItem)
            true
        } else {
            _loading.value = false
            false
        }
    }

    private fun addToCart(newItem: Item) {
        //get instance of cart list(empty list if null value from live data) and check for empty
        //if empty, just add new item
        val cartList = _cart.value?.toMutableList() ?: mutableListOf()
        if (!cartList.isNullOrEmpty()) {
            //iterate through list and check if item being added already exists
            //if yes, increment quantity of existing item, otherwise add item as new
            var updated = false
            cartList.forEachIndexed { index, i ->
                i.takeIf { it.item.id == newItem.id}?.let {
                    Log.v("ViewModel", "item with id ${newItem.id} exists")
                    i.quantity++
                    updated = true
                }
            }
            if (!updated) {
                cartList.add(CartItem(1, newItem))
            }
        } else {
            cartList.add(CartItem(1, newItem))
        }

        //update live data values and recalculate total
        _loading.value = false
        _cart.value = cartList
        _cartAction.value = ActionType.Add
        calculateTotal()
    }

    fun removeFromCart(index: Int) {
        _loading.value = true

        //get instance of cart list but only remove if index exists
        val cartList = _cart.value?.toMutableList() ?: mutableListOf()
        if (index < cartList.size) {
            lastRemovedItem = cartList[index]
            cartList.removeAt(index)

            //update live data values and recalculate total
            _loading.value = false
            _cart.value = cartList
            _cartAction.value = ActionType.Remove
            calculateTotal()
        }
    }

    fun undoRemoveItem() {
        //get instance of cart list, add saved instance of last removed item and update live data
        val cartList = _cart.value?.toMutableList() ?: mutableListOf()
        cartList.add(lastRemovedItem)
        _loading.value = false
        _cart.value = cartList
        calculateTotal()
    }

    private fun calculateTotal() {
        var total = 0.0
        cart.value?.forEach {
            total += ItemUtils.getItemPrice(it)
        }

        _total.value = StringBuilder()
                .append("Total: $")
                .append(ItemUtils.formatToCurrency(total))
                .toString()
    }

}