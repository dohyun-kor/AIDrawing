package com.example.gametset.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.databinding.StoreAdapterItemBinding
import com.example.gametset.room.model.dto.StoreDto

class StoreAdapter : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    private var items: List<StoreDto.ItemData> = emptyList()

    inner class ViewHolder(private val binding: StoreAdapterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreDto.ItemData) {
            binding.item = item
            binding.executePendingBindings()
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
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<StoreDto.ItemData>) {
        items = newItems
        notifyDataSetChanged()
    }
}