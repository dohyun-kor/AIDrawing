package com.example.gametset.room.ui.store

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.gametset.databinding.FragmentStoreModalBinding
import com.example.gametset.room.data.local.SharedPreferencesUtil
import com.example.gametset.room.data.model.dto.StoreDto
import com.example.gametset.room.data.remote.RetrofitUtil
import kotlinx.coroutines.launch
import com.example.gametset.room.data.model.dto.PurchaseRequest
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel


class StoreModalFragment : DialogFragment() {
    private val viewModel : MainActivityViewModel by activityViewModels()
    private var _binding: FragmentStoreModalBinding? = null
    private val binding get() = _binding!!

    private var currentItem: StoreDto? = null  // 현재 아이템 정보 저장용 변수 추가

    fun loadItemData(itemId: Int) {
        lifecycleScope.launch {
            try {
                Log.d("StoreModal", "단일 조회 시작 - itemId: $itemId")
                val response = RetrofitUtil.storeService.getOneItem(itemId)
                Log.d("StoreModal", "API 응답: $response")
                
                currentItem = response  // 아이템 정보 저장
                binding.item = response
                Log.d("StoreModal", "데이터 바인딩 완료: $response")
                
                setupPurchaseButton()  // 구매 버튼 설정
                
            } catch (e: Exception) {
                Log.e("StoreModal", "API 호출 실패", e)
                Log.e("StoreModal", "에러 메시지: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun setupPurchaseButton() {
        binding.purchaseButton.setOnClickListener {
            currentItem?.let { item ->
                purchaseItem(item.itemId)
            }
        }
    }

    private fun purchaseItem(itemId: Int) {
        val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
        val user = sharedPreferencesUtil.getUser()
        
        binding.purchaseButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val purchaseRequest = PurchaseRequest(
                    itemId = itemId,
                    userId = user.userId,
                    itemPrice = currentItem!!.price,
                    category = currentItem!!.category
                )
                
                val response = RetrofitUtil.myItemService.purchase(purchaseRequest)
                
                if (response) {
                    Log.d("StoreModal", "구매 성공")
                    
                    // 이미 선언된 viewModel 사용
                    viewModel.userInfo(user.userId)
                    
                    dismiss()
                    
                    // StoreFragment 찾아서 새로고침
                    (activity as? FragmentActivity)?.supportFragmentManager?.fragments?.forEach { fragment ->
                        if (fragment is StoreFragment) {
                            fragment.refreshStore()
                        }
                    }
                } else {
                    Log.d("StoreModal", "구매 실패")
                    binding.purchaseButton.isEnabled = true
                }
            } catch (e: Exception) {
                Log.e("StoreModal", "구매 처리 실패", e)
                binding.purchaseButton.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnClickListener {
            dismiss()
        }

        binding.modalBody.setOnClickListener { }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}