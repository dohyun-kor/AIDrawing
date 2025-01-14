package com.ssafy.smartstore_jetpack.ui.order

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.data.model.dto.Order
import com.ssafy.smartstore_jetpack.data.model.dto.OrderDetail
import com.ssafy.smartstore_jetpack.data.model.dto.ShoppingCart
import com.ssafy.smartstore_jetpack.data.remote.RetrofitUtil
import com.ssafy.smartstore_jetpack.databinding.FragmentOrderBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import com.ssafy.smartstore_jetpack.util.CommonUtils
import kotlinx.coroutines.launch
import kotlin.math.*

// 하단 주문 탭
private const val TAG = "OrderFragment_싸피"

class OrderFragment :
    BaseFragment<FragmentOrderBinding>(FragmentOrderBinding::bind, R.layout.fragment_order) {
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var mainActivity: MainActivity

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var totalprice : Int = 0
    private var coinamount : Int = 0

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        activityViewModel.getUserInfo(user.id)
        registerObserver()
        initAdapter()
        refreshList()

        binding.btnPurchase.setOnClickListener {
            if(activityViewModel.shoppingList.size <= 0){
                showToast("Your shopping cart is empty")
            }else{
                val user = ApplicationClass.sharedPreferencesUtil.getUser()
                var orderdetailList = ArrayList<OrderDetail>()
                var cnt = 0
                activityViewModel.shoppingList.forEach {
                    val orderdetail = OrderDetail(it.menuId,it.menuCnt,it.size,it.shot)
                    cnt += it.menuCnt
                    orderdetailList.add(orderdetail)
                }
                Log.d(TAG, "coinAmount@@: $coinamount")
                val order = Order(0, user.id, "Take out 주문", "","N", orderdetailList, coinamount)
                activityViewModel.ordertoServer(order)
                activityViewModel.updatecoin(user.id, -coinamount)
                activityViewModel.updatecoin(user.id, cnt*100)
                activityViewModel.shoppingList.clear()
                refreshList()
                showToast("Your order has been completed")
            }
        }

        binding.coincheck.setOnClickListener{
            settextvalue()
        }

    }

    private fun initAdapter() {
        shoppingListAdapter = ShoppingListAdapter(
            list = mutableListOf(),  // 데이터 목록
            MinusClickListener = { item ->
                activityViewModel.minusShoppingList(item)
                refreshList()
            },
            PlusClickListener = { item ->
                activityViewModel.addShoppingList(item)
                refreshList()
            }
        )

        binding.recyclerViewShoppingList.apply {
            adapter = shoppingListAdapter
            //원래의 목록위치로 돌아오게함
            adapter?.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }


    private fun refreshList() {
        mainActivity.updateBadge()
        settextvalue()
        shoppingListAdapter.list = activityViewModel.shoppingList
        shoppingListAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun settextvalue(){
        totalprice = 0
        activityViewModel.shoppingList.forEach {
            totalprice += (it.menuPrice + it.size + it.shot) * it.menuCnt
        }

        if(binding.coincheck.isChecked){
            if(totalprice == 0) {
                binding.btnPurchase.text = "BUY"
                coinamount = 0
            }
            else if(activityViewModel.coin.value!! < totalprice){
                totalprice -= activityViewModel.coin.value!!
                coinamount = activityViewModel.coin.value!!
                binding.btnPurchase.text = CommonUtils.makeComma(totalprice) + "     BUY"
            }
            else if(activityViewModel.coin.value!! > totalprice){
                coinamount = totalprice
                totalprice = 0
                binding.btnPurchase.text = "BUY"
            }
        }
        else{
            if(totalprice == 0){
                binding.btnPurchase.text = "BUY"
                coinamount = 0
            }else{
                binding.btnPurchase.text = CommonUtils.makeComma(totalprice) + "     BUY"
                coinamount = 0
            }
        }

    }

    @SuppressLint("SetTextI18n")
    fun registerObserver(){
        activityViewModel.coin.observe(viewLifecycleOwner){
            binding.userCoin.text = "${it} coins"
        }
    }

}