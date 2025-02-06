package com.example.gametset.room.ui.lobby

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gametset.R
import com.example.gametset.databinding.FragmentLoginBinding
import com.example.gametset.databinding.FragmentSettingModalBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.ui.login.LoginFragmentViewModel

class SettingModalFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentSettingModalBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingModalBinding.inflate(inflater, container, false)
        return binding.root
    }
}