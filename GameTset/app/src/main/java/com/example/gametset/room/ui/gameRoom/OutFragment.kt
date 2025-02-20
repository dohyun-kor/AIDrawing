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
import com.example.gametset.databinding.FragmentOutBinding
import com.example.gametset.databinding.FragmentPlayGameBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass
import org.json.JSONArray

class OutFragment : DialogFragment() {

    private lateinit var binding: FragmentOutBinding
    private var listener: OutDialogListener? = null

    interface OutDialogListener {
        fun onConfirmExit()
    }

    fun setOutDialogListener(listener: OutDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.outbtn.setOnClickListener {
            listener?.onConfirmExit()
            dismiss()
        }

        binding.cancelbtn.setOnClickListener {
            dismiss()
        }
    }
}
