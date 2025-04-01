package com.example.gametset.room.ui.gameRoom

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.gametset.R
import com.example.gametset.databinding.FragmentGameWinnerBinding
import com.example.gametset.databinding.FragmentPlayGameBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass
import org.json.JSONArray

class GameWinnerFragment : DialogFragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentGameWinnerBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameWinnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 다이얼로그 크기 설정
//        dialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )

        binding.tvUsername.text = arguments?.getString("nickname") ?: ""
        binding.tvScore.text = (arguments?.getInt("score") ?: 0).toString()

        binding.btnClose.setOnClickListener{
            dismiss()
        }
    }
}