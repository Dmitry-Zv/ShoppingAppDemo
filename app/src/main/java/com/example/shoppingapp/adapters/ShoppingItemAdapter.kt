package com.example.shoppingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.shoppingapp.data.local.ShoppingItem
import com.example.shoppingapp.databinding.ItemShoppingBinding
import javax.inject.Inject

class ShoppingItemAdapter @Inject constructor(private val glide: RequestManager) :
    RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder>() {


    inner class ShoppingItemViewHolder(val binding: ItemShoppingBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val callback = object : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, callback)

    var shoppingItems: List<ShoppingItem>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShoppingBinding.inflate(inflater, parent, false)
        return ShoppingItemViewHolder(binding)
    }

    override fun getItemCount(): Int = shoppingItems.size


    override fun onBindViewHolder(holder: ShoppingItemViewHolder, position: Int) {
        val shoppingItem = shoppingItems[position]
        with(holder.binding) {
            tvName.text = shoppingItem.name
            tvShoppingItemAmount.text = "${shoppingItem.amount}x"
            tvShoppingItemPrice.text = "${shoppingItem.price}$"
            glide.load(shoppingItem.imageUrl).into(ivShoppingImage)
        }

    }
}