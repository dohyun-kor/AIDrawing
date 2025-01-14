package com.ssafy.smartstore_jetpack.ui.my

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.data.model.response.OrderResponse
import com.ssafy.smartstore_jetpack.data.remote.RetrofitUtil
import com.ssafy.smartstore_jetpack.databinding.FragmentOrderDetailBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import com.ssafy.smartstore_jetpack.util.CommonUtils
import kotlinx.coroutines.launch

private const val TAG = "OrderDetailFragment_싸피"
class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>(
    FragmentOrderDetailBinding::bind,
    R.layout.fragment_order_detail
) {
    private lateinit var orderDetailListAdapter: OrderDetailListAdapter
    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        activityViewModel.myPageOrderId.observe(viewLifecycleOwner){
            initData(it)
        }
    }

    private fun initAdapter(){
        orderDetailListAdapter = OrderDetailListAdapter(mainActivity, OrderResponse())

        binding.recyclerViewOrderDetailList.apply {
            adapter = orderDetailListAdapter
        }
    }

    private fun initData(orderId:Int) {
        lifecycleScope.launch{
            RetrofitUtil.orderService.getOrderDetail(orderId).let{
                orderDetailListAdapter.orderResponse = CommonUtils.calcTotalPrice(it)
                orderDetailListAdapter.notifyDataSetChanged()
                setOrderDetailScreen(it)
            }
        }
    }

    // OrderDetail 페이지 화면 구성
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setOrderDetailScreen(orderResponse: OrderResponse) {
        binding.tvOrderStatus.text = CommonUtils.isOrderCompleted(orderResponse)
        binding.tvOrderDate.text = CommonUtils.dateformatYMDHM(orderResponse.orderDate)
        binding.tvTotalPrice.text = CommonUtils.makeComma(orderResponse.totalPrice)
        Log.d(TAG, "setOrderDetailScreen: ${orderResponse.details[0]}")
        if(orderResponse.details[0].coin > 0){
            binding.coinuse.visibility = View.VISIBLE
            binding.coinuse.text = "Use ${orderResponse.details[0].coin} coins"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }
}