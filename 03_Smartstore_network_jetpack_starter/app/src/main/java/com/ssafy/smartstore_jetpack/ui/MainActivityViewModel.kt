package com.ssafy.smartstore_jetpack.ui

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.data.model.dto.Order
import com.ssafy.smartstore_jetpack.data.model.dto.ShoppingCart
import com.ssafy.smartstore_jetpack.data.model.response.OrderResponse
import com.ssafy.smartstore_jetpack.data.model.response.UserResponse
import com.ssafy.smartstore_jetpack.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel_싸피"
class MainActivityViewModel : ViewModel() {

    var cartItemCount = 0
    fun refreshBadge(){
        var n = 0
        shoppingList.forEach {
            n += it.menuCnt
        }
        cartItemCount = n
    }

    // my 페이지에서 detail로 이동시 사용
    private val _myPageOrderId = MutableLiveData<Int>()
    val myPageOrderId: LiveData<Int>
        get() = _myPageOrderId

    fun setOrderId(orderId:Int){
        _myPageOrderId.value = orderId
    }

    // 선택된 productId
    private val _productId = MutableLiveData<Int>()
    val productId: LiveData<Int>
        get() = _productId


    fun setProductId(productId:Int){
        _productId.value = productId
    }

    // 장바구니
    val shoppingList = mutableListOf<ShoppingCart>()

    fun repurchase(shoppingCartlist: List<ShoppingCart>){
        shoppingCartlist.forEach {
            addShoppingList(it)
        }
    }

    fun addShoppingList(shoppingCart: ShoppingCart){
        val position = checkDuplication(shoppingCart)
        if (position == -1) {
            shoppingList.add(shoppingCart)
        } else {
            shoppingList[position].addDupMenu(shoppingCart)
        }
    }



    fun minusShoppingList(shoppingCart: ShoppingCart){
        shoppingList.forEach {
            if(it.menuName == shoppingCart.menuName && it.size == shoppingCart.size && it.shot == shoppingCart.shot){
                it.menuCnt--
                if(it.menuCnt == 0){
                    shoppingList.remove(it)
                    return
                }
            }
        }
    }

    private fun checkDuplication(curitem: ShoppingCart): Int {
        var position = -1
        shoppingList.forEachIndexed { index, item ->
            if (item.menuName == curitem.menuName && item.size == curitem.size && item.shot == curitem.shot)
                position = index
        }
        return position
    }

    fun deleteShoppingList(itemName: String){
        val numberList = arrayListOf<Int>()
        shoppingList.forEachIndexed{ index, item ->
            if(item.menuName == itemName){
                numberList.add(index)
            }
        }
        numberList.forEachIndexed { index, i ->
            shoppingList.removeAt(i)
        }
    }

    fun ordertoServer(order : Order){
        viewModelScope.launch {
            RetrofitUtil.orderService.makeOrder(order)
        }
    }

    private val _sixmonthorder = MutableLiveData<List<OrderResponse>>()
    val sixmonthorder : LiveData<List<OrderResponse>>
        get() = _sixmonthorder

    fun getsixmonthorder(id:String){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.orderService.getLast6MonthOrder(id)
            }.onSuccess {
                _sixmonthorder.value = it
            }.onFailure {
                _sixmonthorder.value = listOf()
            }
        }
    }

    // ViewModel에서 userinfo를 MutableLiveData로 선언
    val userinfo = MutableLiveData<UserResponse>()

    fun getUserInfo(id: String) {
        viewModelScope.launch {
            val result = RetrofitUtil.userService.getUserInfo(id)
            userinfo.value = result // userinfo에 값을 설정하여 변경 알림
            _coin.value = result.user.coin
        }
    }

    private val _isTagged = MutableLiveData<Pair<Boolean, String?>>()
    val isTagged: LiveData<Pair<Boolean, String?>> = _isTagged

    fun changeState(tagged: Boolean, tableNumber: String? = null) {
        _isTagged.value = Pair(tagged, tableNumber)
    }

    fun resetTag() {
        _isTagged.value = Pair(false, null)
    }

    private val _coin = MutableLiveData<Int>()
    val coin: LiveData<Int>
        get() = _coin

    fun updatecoin(id: String, coin: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.userService.updatecoin(id, coin)
            }.onSuccess {
                getUserInfo(id)
                _coin.value = userinfo.value?.user?.coin
            }.onFailure {

            }
        }
    }
}