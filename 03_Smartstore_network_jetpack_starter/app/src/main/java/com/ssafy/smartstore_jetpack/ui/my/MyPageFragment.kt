package com.ssafy.smartstore_jetpack.ui.my

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.databinding.FragmentMypageBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import com.ssafy.smartstore_jetpack.util.CommonUtils

private const val TAG = "MyPageFragment_싸피"
class MyPageFragment : BaseFragment<FragmentMypageBinding>(
    FragmentMypageBinding::bind,
    R.layout.fragment_mypage
){
    private var orderAdapter : OrderListAdapter = OrderListAdapter(emptyList())
    private lateinit var mainActivity: MainActivity

    private val activityViewModel : MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityViewModel.sixmonthorder.observe(viewLifecycleOwner){
            orderAdapter.list = CommonUtils.calcTotalPrice(it)
            refreshList()
        }

        val userid = ApplicationClass.sharedPreferencesUtil.getUser().id
        getUserData(userid)
        initOrderData(userid)
        initAdapter()
    }


    private fun initAdapter(){
        orderAdapter.setItemClickListener(object : OrderListAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, orderid: Int) {
                activityViewModel.setOrderId(orderid)
                mainActivity.openFragment(2)
            }
        })

        binding.recyclerViewOrder.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = orderAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUserData(id: String) {
        activityViewModel.getUserInfo(id)
        // userinfo의 변경을 관찰하고 값이 설정되면 UI 업데이트
        activityViewModel.userinfo.observe(viewLifecycleOwner) { curuser ->
            Log.d(TAG, "getUserData: $curuser")
//            binding.tvMypageOrderEmpty.visibility = View.GONE
            // grade.img에 저장된 파일 이름을 통해 drawable 이미지를 설정
            val imageName = curuser.grade.img // 예: "seeds.png"
            val resourceId = context?.resources?.getIdentifier(
                imageName.substringBeforeLast("."),
                "drawable",
                context?.packageName
            )
            binding.nickname.text = curuser.user.name
            binding.userCoin.text = curuser.user.coin.toString() + " Coin"
        }
    }


    private fun initOrderData(id:String){
        // 최근 6개월 주문내역
        activityViewModel.getsixmonthorder(id)
    }

    private fun refreshList() {
        orderAdapter.notifyDataSetChanged()
    }
}