package com.example.gametset.room.ui.lobby

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gametset.databinding.FragmentMenuPopUpBinding
import com.example.gametset.room.MainActivity

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
            //설정
            settingsFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            //친구
            friendFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
                mainActivity.openFragment(6)
            }

            //랭킹
            rankingFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            // 로그아웃
            logoutFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
                mainActivity.logout()
            }

            //배경 눌렀을때 뒤로 가기
            popupBackground.setOnClickListener{
                parentFragmentManager.popBackStack()
            }
        }
    }
}