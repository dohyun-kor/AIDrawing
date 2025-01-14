package com.ssafy.smartstore_jetpack.ui.home

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
import com.ssafy.smartstore_jetpack.data.model.response.OrderResponse
import com.ssafy.smartstore_jetpack.ui.my.OrderListAdapter
import com.ssafy.smartstore_jetpack.util.CommonUtils

private const val TAG = "HomeListAdapter_싸피"
class HomeListAdapter(var list:List<OrderResponse>) :
    RecyclerView.Adapter<HomeListAdapter.OrderHolder>(){

    inner class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val menunumber = itemView.findViewById<TextView>(R.id.orderId)
        val textMenuNames = itemView.findViewById<TextView>(R.id.textMenuNames)
        val textMenuPrice = itemView.findViewById<TextView>(R.id.textMenuPrice)
        val textMenuDate = itemView.findViewById<TextView>(R.id.textMenuDate)
        val goorder = itemView.findViewById<ImageView>(R.id.btnGoOrder)

        fun bindInfo(data: OrderResponse){
            Log.d(TAG, "bindInfo at HomeFragment: ${data}")

            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${data.details[0].productImg}")
                .override(350, 350) // 고정된 크기로 설정
                .into(menuImage)

            if(data.orderCount > 1){
                textMenuNames.text = "${data.details[0].productName} 외 ${data.orderCount -1}건"  //외 x건
            }else{
                textMenuNames.text = data.details[0].productName
            }

            textMenuPrice.text = CommonUtils.makeComma(data.totalPrice)
            menunumber.text = data.orderId.toString()
            textMenuDate.text = CommonUtils.dateformatYMDHM(data.orderDate)
            //클릭연결

            goorder.setOnClickListener{
                Log.d(TAG, "bindInfo: ${data.orderId}")
                itemClickListner.onClick(it, layoutPosition, data.orderId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_latest_order, parent, false)
        return OrderHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View, position: Int, orderid:Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}