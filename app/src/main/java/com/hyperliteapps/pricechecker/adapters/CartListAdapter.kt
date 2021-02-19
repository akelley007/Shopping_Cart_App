package com.hyperliteapps.pricechecker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperliteapps.pricechecker.databinding.ShoppingCartItemBinding
import com.hyperliteapps.pricechecker.models.CartItem

class CartListAdapter : RecyclerView.Adapter<CartListAdapter.ItemViewHolder>() {
    var cartItems: MutableList<CartItem> = mutableListOf()

    class ItemViewHolder(val binding: ShoppingCartItemBinding): RecyclerView.ViewHolder(binding.root)

    fun updateCart(items: List<CartItem>) {
        cartItems = items.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ShoppingCartItemBinding.inflate(layoutInflater, parent, false)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = cartItems.get(position)
        holder.binding.item = currentItem
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}