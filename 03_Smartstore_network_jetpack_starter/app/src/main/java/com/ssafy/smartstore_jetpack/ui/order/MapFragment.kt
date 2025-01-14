package com.ssafy.smartstore_jetpack.ui.order

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.ui.MainActivity

// Order 탭 - 지도 화면
class MapFragment : Fragment(){
    private lateinit var mainActivity: MainActivity
    private lateinit var map : ImageView
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_old, container, false)
        map = view.findViewById(R.id.map_view_map_google_map)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map.setOnClickListener {
            showDialogStore()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }
    private fun showDialogStore() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setView(R.layout.dialog_call)
            setTitle("매장 상세")
            setCancelable(true)
            setPositiveButton("전화걸기") { dialog, _ ->
                dialog.cancel()
            }
            setNegativeButton("길찾기") { dialog, _ ->
                dialog.cancel()
            }
        }
        builder.create().show()
    }
}