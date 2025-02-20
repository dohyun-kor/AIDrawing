package com.example.gametset.room.ui.store

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


//getAllItems()에서 받은 response_item 데이터가 storeAdapter.submitList()를 통해 어댑터로 전달됨
//어댑터는 이 데이터를 각 아이템의 레이아웃 (store_adapter_item.xml)에 바인딩
//XML에서 app:imageUrl="@{item.link}" 구문을 만나면 자동으로 BindingAdapter가 호출됨
// 홀리 홀리 하구만요
object StoreBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(url)
                .into(view)
        }
    }
}