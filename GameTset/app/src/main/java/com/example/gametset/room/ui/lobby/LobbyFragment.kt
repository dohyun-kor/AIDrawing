package com.example.gametset.room.ui.lobby

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gametset.R
import com.example.gametset.room.MainActivity

class LobbyFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_page_2
        mainActivity.hideBottomNav(false)
        mainActivity.hideToolBar(false)
    }

    override fun onStop() {
        super.onStop()
        mainActivity.hideBottomNav(true)
        mainActivity.hideToolBar(true)
    }

}