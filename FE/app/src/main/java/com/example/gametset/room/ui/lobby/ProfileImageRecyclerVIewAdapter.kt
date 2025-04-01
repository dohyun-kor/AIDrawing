package com.example.gametset.room.ui.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gametset.databinding.ProfileAdapterItemBinding
import com.example.gametset.room.data.model.dto.StoreDto

class ProfileImageRecyclerVIewAdapter(private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<ProfileImageRecyclerVIewAdapter.ViewHolder>() {

    private var itemList: List<StoreDto> = listOf()

    inner class ViewHolder(private val binding: ProfileAdapterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreDto) {
            Glide.with(binding.root)
                .load(item.link)
                .into(binding.profileItemImage)

            binding.profileItemText.setOnClickListener {
                onItemClick(item.itemId)
            }
            binding.profileItemImage.setOnClickListener{
                onItemClick(item.itemId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProfileAdapterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(items: List<StoreDto>) {
        itemList = items
        notifyDataSetChanged()
    }
}