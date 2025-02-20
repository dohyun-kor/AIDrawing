package com.example.gametset.room.ui.store

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.databinding.StoreAdapterItemBinding
import com.example.gametset.room.data.model.dto.StoreDto
import com.example.gametset.room.data.model.dto.MyItemDto

class StoreAdapter : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    private var items: List<StoreDto> = emptyList()
    private var filteredItems: List<StoreDto> = emptyList()
    private var purchasedItemIds = mutableListOf<Int>()  // String에서 Int로 변경
    private var onItemClickListener: ((StoreDto) -> Unit)? = null

    inner class ViewHolder(private val binding: StoreAdapterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreDto) {
            // 기존 바인딩 초기화
            binding.apply {
                this.item = item
                
                // 이미지 크기 초기화
                storeItemBody.post {
                    if (storeItemBody.width == 0) {  // 이미지가 작아진 경우
                        storeItemBody.requestLayout()
                        root.requestLayout()
                    }
                }

                // 구매 여부 확인
                val isPurchased = purchasedItemIds.contains(item.itemId)
                
                if (isPurchased) {
                    purchasedOverlay.visibility = View.VISIBLE
                    purchasedText.visibility = View.VISIBLE
                    storeItemBody.isClickable = false
                } else {
                    purchasedOverlay.visibility = View.GONE
                    purchasedText.visibility = View.GONE
                    storeItemBody.isClickable = true
                    
                    storeItemBody.setOnClickListener {
                        val modalFragment = StoreModalFragment()
                        modalFragment.loadItemData(item.itemId)

                        val activity = root.context as FragmentActivity
                        activity.supportFragmentManager.beginTransaction()
                            .add(android.R.id.content, modalFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
                
                executePendingBindings()
            }
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
        filteredItems = newItems  // 모든 아이템 표시
        notifyDataSetChanged()
    }

    fun filterByCategory(category: String?) {
        filterItems(category)
    }

    fun setOnItemClickListener(listener: (StoreDto) -> Unit) {
        onItemClickListener = listener
    }

    // 구매한 아이템 목록 설정
    fun setPurchasedItems(myItems: List<MyItemDto>) {
        purchasedItemIds = myItems.map { it.itemId }.toMutableList()
        filterItems(null)
    }

    // 실제 필터링 로직
    private fun filterItems(category: String?) {
        filteredItems = items.filter { item -> 
            category == null || item.category == category
        }
        
        // 전체 레이아웃 갱신
        notifyDataSetChanged()
        
        // 약간의 지연 후 레이아웃 다시 그리기
        Handler(Looper.getMainLooper()).postDelayed({
            notifyItemRangeChanged(0, filteredItems.size)
        }, 100)
    }
}