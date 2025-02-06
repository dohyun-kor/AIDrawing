package com.example.gametset.room.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.databinding.StoreAdapterItemBinding
import com.example.gametset.room.data.model.dto.StoreDto

class StoreAdapter : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    private var items: List<StoreDto> = emptyList()
    private var filteredItems: List<StoreDto> = emptyList()

    inner class ViewHolder(private val binding: StoreAdapterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreDto) {
            binding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoreAdapterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount(): Int = filteredItems.size

    fun submitList(newItems: List<StoreDto>) {
        items = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }

    fun filterByCategory(category: String?) {
        filteredItems = if (category == null) {
            items
        } else {
            items.filter { it.category == category }
        }
        notifyDataSetChanged()
    }
}