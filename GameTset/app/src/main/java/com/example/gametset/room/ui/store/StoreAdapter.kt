package com.example.gametset.room.ui.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.databinding.StoreAdapterItemBinding
import com.example.gametset.room.data.model.dto.StoreDto

class StoreAdapter : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    private var items: List<StoreDto> = emptyList()
    private var filteredItems: List<StoreDto> = emptyList()
    private var onItemClickListener: ((StoreDto) -> Unit)? = null

    inner class ViewHolder(private val binding: StoreAdapterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreDto) {
            binding.item = item
            binding.storeItemVector1.setOnClickListener {
                val activity = binding.root.context as FragmentActivity
                activity.supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, StoreModalFragment())
                    .addToBackStack(null)
                    .commit()
            }
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

    fun setOnItemClickListener(listener: (StoreDto) -> Unit) {
        onItemClickListener = listener
    }
}