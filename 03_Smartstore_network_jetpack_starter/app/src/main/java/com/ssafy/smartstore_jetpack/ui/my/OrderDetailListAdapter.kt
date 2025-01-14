package com.ssafy.smartstore_jetpack.ui.my

import android.content.Context
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
import com.ssafy.smartstore_jetpack.data.model.response.OrderDetailResponse
import com.ssafy.smartstore_jetpack.data.model.response.OrderResponse
import com.ssafy.smartstore_jetpack.util.CommonUtils

private const val TAG = "OrderDetailListAdapter_싸피"
class OrderDetailListAdapter(val context: Context, var orderResponse: OrderResponse) :
    RecyclerView.Adapter<OrderDetailListAdapter.OrderDetailListHolder>(){

    inner class OrderDetailListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val textShoppingMenuName = itemView.findViewById<TextView>(R.id.textShoppingMenuName)
        val textShoppingMenuCount = itemView.findViewById<TextView>(R.id.textShoppingMenuCount)
        val textShoppingMenuMoneyAll = itemView.findViewById<TextView>(R.id.textShoppingMenuMoneyAll)
        val textShoppingMenuSize = itemView.findViewById<TextView>(R.id.OrderDetailSize)
        val textShoppingMenuShot = itemView.findViewById<TextView>(R.id.OrderDetailShot)

        fun bindInfo(data: OrderDetailResponse){
            val type = if(data.quantity>1){
                if(data.productType =="food"){
                    "plates"
                }else{
                    "drinks"
                }
            }else{
                if(data.productType =="food"){
                    "plate"
                }else{
                    "drink"
                }
            }
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${data.productImg}")
                .into(menuImage)
            Log.d(TAG, "data is ${data}")
            textShoppingMenuName.text = data.productName
            textShoppingMenuCount.text = "${data.quantity} $type"
            textShoppingMenuMoneyAll.text = CommonUtils.makeComma(data.sumPrice)
            textShoppingMenuSize.text = when(data.csize){
                500 -> {
                    if(data.productType =="food"){
                        "Small"
                    }else{
                        "Tall"
                    }
                }
                1000 -> {
                    if(data.productType =="food"){
                        "Medium"
                    }else{
                        "Grande"
                    }
                }
                else -> {
                    if(data.productType =="food"){
                        "Large"
                    }else{
                        "Venti"
                    }
                }
            }
            textShoppingMenuShot.text = when(data.cshot){
                500 -> {
                    "Single"
                }
                else -> {
                    if(data.productType =="food"){
                        "Combo"
                    }else{
                        "Double"
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailListHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_order_detail_list, parent, false)
        return OrderDetailListHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailListHolder, position: Int) {
        holder.bindInfo(orderResponse.details[position])
    }

    override fun getItemCount(): Int {
        return orderResponse.details.size
    }
}