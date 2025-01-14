package com.ssafy.smartstore_jetpack.ui.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.databinding.FragmentGameBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel

class GameFragment : BaseFragment<FragmentGameBinding>(
    FragmentGameBinding::bind,
    R.layout.fragment_game
) {

    private lateinit var mainActivity: MainActivity
    private val mainActivityViewModel : MainActivityViewModel by viewModels()

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        mainActivityViewModel.getUserInfo(user.id)
        registerObserver()

        // 버튼 클릭 이벤트 설정
        binding.quiz.setOnClickListener {
            val fragment = CatchFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, fragment)
                .addToBackStack("QuizFragment")
                .commit()
            mainActivity.hideActionBar(true)
            mainActivity.hideBottomNav(true)
        }

        binding.box.setOnClickListener {
            val fragment = BoxFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, fragment)
                .addToBackStack("RandomBoxFragment")
                .commit()
            mainActivity.hideActionBar(true)
            mainActivity.hideBottomNav(true)
        }

        binding.roulette.setOnClickListener {
            val fragment = RouletteFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, fragment)
                .addToBackStack("RouletteFragment")
                .commit()
            mainActivity.hideActionBar(true)
            mainActivity.hideBottomNav(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    @SuppressLint("SetTextI18n")
    fun registerObserver(){
        mainActivityViewModel.coin.observe(viewLifecycleOwner){
            binding.coinCount.text = "${it} coins"
        }
    }
}
