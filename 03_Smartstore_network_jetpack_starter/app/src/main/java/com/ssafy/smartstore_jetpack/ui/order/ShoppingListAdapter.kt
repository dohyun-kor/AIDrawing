package com.ssafy.smartstore_jetpack.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.databinding.ListItemShoppingListBinding
import com.ssafy.smartstore_jetpack.data.model.dto.ShoppingCart

private const val TAG = "ShoppingListAdapter_싸피"
class ShoppingListAdapter(
    var list: MutableList<ShoppingCart>,
    private val MinusClickListener: (ShoppingCart) -> Unit,
    private val PlusClickListener: (ShoppingCart) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>() {

    inner class ShoppingListHolder(private val binding: ListItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(position: Int) {
            val item = list[position]

            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${item.menuImg}")
                .into(binding.menuImage)
            binding.textShoppingMenuName.text = item.menuName
            binding.textShoppingMenuCount.text = "${item.menuCnt}"
            Log.d(TAG, "bindInfo: ${item.menuPrice} and ${item.size} and ${item.shot}")
            binding.textShoppingMenuMoney.text = "₩ ${item.menuPrice +item.size + item.shot}"
            binding.textShoppingMenuSize.text = when(item.size){
                500 -> {
                    if(item.type == "food"){
                        "Small"
                    }else{
                        "Tall"
                    }
                }
                1000 -> {
                    if(item.type == "food"){
                        "Medium"
                    }else{
                        "Grande"
                    }
                }
                else -> {
                    if(item.type == "food"){
                        "Large"
                    }else{
                        "Venti"
                    }
                }
            }
            binding.textShoppingMenuShot.text = when(item.shot){
                500 -> {
                    "Single"
                }
                else -> {
                    if(item.type == "food"){
                        "Combo"
                    }else{
                        "Double"
                    }

                }
            }


//            binding.textShoppingMenuMoneyAll.text = "${item.totalPrice} 원"
            binding.btnPlus.setOnClickListener {
                PlusClickListener(item)
            }
            binding.btnMinus.setOnClickListener {
                MinusClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
        return ShoppingListHolder(
            ListItemShoppingListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ShoppingListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}