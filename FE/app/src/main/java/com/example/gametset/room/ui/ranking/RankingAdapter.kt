package com.example.gametset.room.ui.ranking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.databinding.ItemRankingBinding
import com.example.gametset.ranking.data.model.dto.TopRankingListDto

class RankingAdapter : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    private var rankings = listOf<TopRankingListDto>()

    inner class ViewHolder(private val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ranking: TopRankingListDto, position: Int) {
            binding.apply {
                rankPositionText.text = "${position + 1}"
                nicknameText.text = ranking.nickname
                expText.text = "${ranking.exp} EXP"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rankings[position], position)
    }

    override fun getItemCount() = rankings.size

    fun submitList(newRankings: List<TopRankingListDto>) {
        rankings = newRankings
        notifyDataSetChanged()
    }
} 