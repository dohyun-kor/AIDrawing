package com.example.gametset.room.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gametset.R
import android.widget.TextView

class StoreModalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store_modal, container, false)
        
        // 취소 버튼 클릭시 뒤로가기
        view.findViewById<View>(R.id.cancelButton).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    companion object {
        fun newInstance() = StoreModalFragment()
    }
}