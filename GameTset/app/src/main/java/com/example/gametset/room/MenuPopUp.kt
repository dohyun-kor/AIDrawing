package com.example.gametset.room

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.gametset.R
import com.example.gametset.databinding.FragmentLoginBinding
import com.example.gametset.databinding.FragmentMenuPopUpBinding
import com.example.gametset.room.base.ApplicationClass

class MenuPopUp : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentMenuPopUpBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuPopUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            settingsFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            friendFrame.setOnClickListener {
                parentFragmentManager.popBackStack()

            }

            rankingFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            logoutFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            //배경 눌렀을때 뒤로 가기
            popupBackground.setOnClickListener{
                parentFragmentManager.popBackStack()
            }
        }
    }
}