package com.ssafy.smartstore_jetpack.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.data.model.dto.Product

private const val TAG = "MenuAdapter_싸피"
class MenuAdapter(
    private var productList: List<Product>, // 원본 리스트
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {

    private var displayedList: List<Product> = productList // 화면에 보여질 리스트

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val menuName: TextView = itemView.findViewById(R.id.textMenuNames)
        private val menuImage: ImageView = itemView.findViewById(R.id.menuImage)

        fun bindInfo(product: Product) {
            menuName.text = product.name
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${product.img}")
                .override(350, 350)
                .into(menuImage)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onClick(displayedList[position].id) // 클릭한 상품 ID 전달
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_menu, parent, false)
        return MenuHolder(view)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.bindInfo(displayedList[position])
    }

    override fun getItemCount(): Int = displayedList.size

    // 리스트 업데이트
    fun updateList(newProductList: List<Product>) {
        productList = newProductList
        displayedList = newProductList
        notifyDataSetChanged()
    }

    // 이름으로 필터링
    fun filterByName(query: String) {
        val cleanedQuery = query.trim().lowercase()
        displayedList = if (cleanedQuery.isEmpty()) {
            productList
        } else {
            productList.filter { it.name.contains(cleanedQuery, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    // 타입으로 필터링
    fun filterByType(type: String) {
        displayedList = if (type.isEmpty()) {
            productList
        } else {
            productList.filter { it.type.equals(type, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    // 클릭 리스너
    fun interface ItemClickListener {
        fun onClick(productId: Int)
    }
}
