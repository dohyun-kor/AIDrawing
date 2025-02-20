package com.example.gametset.room.ui.store

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.databinding.FragmentLoginBinding
import com.example.gametset.databinding.FragmentStoreBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.local.SharedPreferencesUtil
import com.example.gametset.room.data.model.dto.StoreDto
import com.example.gametset.room.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class StoreFragment : Fragment() {
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var binding: FragmentStoreBinding
    private lateinit var mainActivity: MainActivity
    private val user = ApplicationClass.sharedPreferencesUtil.getUser()
    private var storeFilterVector1state = 1
    private var storeFilterVector2state = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupFilterButtons()
    }

    private fun setupUI() {
        binding.storeFilterVector1.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.signup_button2_selector_js)
    }

    private fun setupRecyclerView() {
        storeAdapter = StoreAdapter()
        binding.storeItem.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = storeAdapter
        }
    }

    private fun setupFilterButtons() {
        binding.storeFilterVector1.setOnClickListener {
            if (storeFilterVector1state != 1) {
                updateFilterState(true)
                storeFilterVector1state = 1
                storeFilterVector2state = 0
                storeAdapter.filterByCategory("1")
            }
        }

        binding.storeFilterVector2.setOnClickListener {
            if (storeFilterVector2state != 1) {
                updateFilterState(false)
                storeFilterVector2state = 1
                storeFilterVector1state = 0
                storeAdapter.filterByCategory("2")
            }
        }
    }

    private fun updateFilterState(isFirstFilter: Boolean) {
        binding.storeFilterVector1.background = ContextCompat.getDrawable(
            requireContext(),
            if (isFirstFilter) R.drawable.signup_button2_selector_js else R.drawable.signup_button2_js
        )
        binding.storeFilterVector2.background = ContextCompat.getDrawable(
            requireContext(),
            if (isFirstFilter) R.drawable.signup_button2_js else R.drawable.signup_button2_selector_js
        )
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
        mainActivity.hideToolBar(false)
        refreshStore()  // 화면이 표시될 때마다 데이터 새로고침
    }

    override fun onStop() {
        super.onStop()
        mainActivity.hideBottomNav(true)
        mainActivity.hideToolBar(true)
    }

    fun refreshStore() {
        lifecycleScope.launch {
            try {
                val storeItems = RetrofitUtil.storeService.getItem()
                val result = RetrofitUtil.myItemService.myItemsList(user.userId)
                
                storeAdapter.setPurchasedItems(result)
                storeAdapter.submitList(storeItems)
                
                // 현재 선택된 필터 상태에 따라 카테고리 필터링
                if (storeFilterVector1state == 1) {
                    storeAdapter.filterByCategory("1")
                    updateFilterState(true)
                } else {
                    storeAdapter.filterByCategory("2")
                    updateFilterState(false)
                }
            } catch (e: Exception) {
                Log.e("Store", "새로고침 실패", e)
            }
        }
    }
}