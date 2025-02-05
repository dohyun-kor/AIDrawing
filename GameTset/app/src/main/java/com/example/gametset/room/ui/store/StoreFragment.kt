package com.example.gametset.room.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.room.data.StoreDatabase

class StoreFragment : Fragment() {
    private lateinit var storeAdapter: StoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.store_item)
        storeAdapter = StoreAdapter()

        // GridLayoutManager를 사용하여 3열로 설정
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = storeAdapter

        // 더미 데이터 설정
        val dummyData = StoreDatabase.generateDummyData()
        storeAdapter.submitList(dummyData)
    }
}