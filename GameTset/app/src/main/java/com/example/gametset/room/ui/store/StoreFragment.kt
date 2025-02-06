package com.example.gametset.room.ui.store

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.databinding.FragmentLoginBinding
import com.example.gametset.databinding.FragmentStoreBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.data.StoreDatabase

class StoreFragment : Fragment() {
    private lateinit var storeAdapter: StoreAdapter
    lateinit var binding:FragmentStoreBinding
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.storeFilterVector1.background= ContextCompat.getDrawable(requireContext(), R.drawable.signup_button2_selector_js)
        var storeFilterVector1state =1
        var storeFilterVector2state =0

        // RecyclerView 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.store_item)
        storeAdapter = StoreAdapter()

        // GridLayoutManager를 사용하여 3열로 설정
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = storeAdapter

        // 더미 데이터 설정 및 초기 필터링
        val dummyData = StoreDatabase.generateDummyData()
        storeAdapter.submitList(dummyData)
        storeAdapter.filterByCategory("one")  // 초기 필터링을 "one" 카테고리로 설정

        binding.storeFilterVector1.setOnClickListener{
            if(storeFilterVector1state!=1){
                binding.storeFilterVector1.background= ContextCompat.getDrawable(requireContext(), R.drawable.signup_button2_selector_js)
                binding.storeFilterVector2.background= ContextCompat.getDrawable(requireContext(),  R.drawable.signup_button2_js)
                storeFilterVector1state=1
                storeFilterVector2state=0
                storeAdapter.filterByCategory("one")  // "one" 카테고리 필터링
            }
        }

        binding.storeFilterVector2.setOnClickListener{
            if(storeFilterVector2state!=1){
                binding.storeFilterVector2.background= ContextCompat.getDrawable(requireContext(), R.drawable.signup_button2_selector_js)
                binding.storeFilterVector1.background= ContextCompat.getDrawable(requireContext(),  R.drawable.signup_button2_js)
                storeFilterVector2state=1
                storeFilterVector1state=0
                storeAdapter.filterByCategory("two")  // "two" 카테고리 필터링
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
        mainActivity.hideToolBar(false)
    }

    override fun onStop() {
        super.onStop()
        mainActivity.hideBottomNav(true)
        mainActivity.hideToolBar(true)
    }
}