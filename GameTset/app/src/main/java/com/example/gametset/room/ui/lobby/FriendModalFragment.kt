package com.example.gametset.room.ui.lobby

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gametset.R
import com.example.gametset.databinding.ActivityMainBinding
import com.example.gametset.databinding.FragmentFriendModalBinding
import com.example.gametset.databinding.FragmentLoginBinding
import com.example.gametset.room.MainActivity

class FriendModalFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentFriendModalBinding

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            closeBtn.setOnClickListener{
                parentFragmentManager.popBackStack()
            }
        }

    }
}