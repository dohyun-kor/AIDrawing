package com.ssafy.smartstore_jetpack.ui.game

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.data.model.dto.User
import com.ssafy.smartstore_jetpack.databinding.FragmentBoxBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import kotlin.random.Random

private const val TAG = "BoxFragment_싸피"

class BoxFragment : BaseFragment<FragmentBoxBinding>(
    FragmentBoxBinding::bind,
    R.layout.fragment_box
) {

    private lateinit var mainActivity: MainActivity
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var user: User
    private var disabledColor: Int = 0
    private var enabledColor: Int = 0
    private var isBoxOpening = false


    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()
        user = ApplicationClass.sharedPreferencesUtil.getUser()
        mainActivityViewModel.getUserInfo(user.id)

        val lottieForeground = binding.randombox

        // foreground를 맨 앞으로 이동
        ViewCompat.setZ(lottieForeground, 2f)

        // AppBar와 BottomNav 숨김
        mainActivity.hideActionBar(true)
        mainActivity.hideBottomNav(true)
        // 확률 다이얼로그 표시
        showProbabilitiesDialog()

        // ColorStateList를 사용하여 활성화/비활성화 색상 정의
        enabledColor = resources.getColor(R.color.sub_theme, null) // 활성화 상태의 색상
        disabledColor = resources.getColor(R.color.disabled_color, null) // 비활성화 상태의 색상

        // Lottie 애니메이션 종료 시점 감지
        binding.randombox.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isBoxOpening = true
                coincheck()
            }

            override fun onAnimationEnd(animation: Animator) {
                isBoxOpening = false
                coincheck()
                initEvent()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        binding.openbtn.setOnClickListener {
            isBoxOpening = true
            coincheck()
            binding.openbtn.isEnabled = false
            binding.openbtn.setCardBackgroundColor(disabledColor)
            binding.randombox.speed = 0.5f
            binding.randombox.playAnimation()
            mainActivityViewModel.updatecoin(user.id, -100)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // AppBar와 BottomNav 다시 표시
        mainActivity.hideActionBar(false)
        mainActivity.hideBottomNav(false)
    }

    @SuppressLint("SetTextI18n")
    fun registerObserver() {
        mainActivityViewModel.coin.observe(viewLifecycleOwner) {
            binding.mycoinBox.text = "${it} coins"
            coincheck()
        }
    }

    fun initEvent() {
        val prob = Random.nextDouble(0.0, 100.0)

        // 확률에 따라 결과 설정
        val result: String
        val coinAmount: Int

        when {
            prob <= 0.5 -> {
                result = "+10000 coin"
                coinAmount = 10000
            }

            prob <= 3.0 -> {
                result = "+1000 coin"
                coinAmount = 1000
            }

            prob <= 8.0 -> {
                result = "+500 coin"
                coinAmount = 500
            }

            prob <= 28.0 -> {
                result = "+100 coin"
                coinAmount = 100
            }

            else -> {
                result = "Nothing"
                coinAmount = 0
            }
        }

        // 결과를 다이얼로그로 표시
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Congratulations!!")
            .setMessage("The selected item: $result")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // 1000coin과 10000coin의 경우 화려한 효과 추가
        if (coinAmount == 1000 || coinAmount == 10000) {
            // 예: Lottie 애니메이션 시작
            if (coinAmount == 10000) {
                binding.con2.visibility = View.VISIBLE
                binding.donghyun.visibility = View.VISIBLE
                binding.hayul.visibility = View.VISIBLE
            }
            binding.luckyguy.visibility = View.VISIBLE
            binding.congratulation.visibility = View.VISIBLE

            // 추가적으로 화려한 효과 (예: 토스트 메시지)
            showToast("Congratulations!! You got $coinAmount coins!!")
        }

        dialog.show()

        // coinAmount 만큼 코인 업데이트 (ViewModel을 통해 서버에 반영하는 부분은 생략)
        mainActivityViewModel.updatecoin(user.id, coinAmount)
    }

    /**
     * 확률 다이얼로그 표시
     */
//    private fun showProbabilitiesDialog() {
//        val dataList = listOf("+10000 coin", "+1000 coin", "+500 coin", "+100 coin", "Nothing")
//        val probabilities = listOf(0.005f, 0.025f, 0.05f, 0.2f, 0.72f) // 각 결과의 확률 (dataList와 순서 동일)
//
//        val probabilitiesText = dataList.zip(probabilities) { data, prob ->
//            "$data | (${(prob * 100).format(2)}%)"
//        }.joinToString("\n--------------------------------------\n")
//
//        val dialog = android.app.AlertDialog.Builder(requireContext())
//            .setTitle("Probability")
//            .setMessage(probabilitiesText)
//            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//            .create()
//
//        dialog.show()
//    }
    fun showProbabilitiesDialog(){
        // AlertDialog 생성
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_probability_basic, null)


        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

            }
            .create()

        // 다이얼로그 표시
        alertDialog.show()
    }



    fun coincheck() {
        if (isBoxOpening) {
            // 박스가 열리는 중이면 버튼 비활성화
            binding.openbtn.isEnabled = false
            binding.openbtn.setCardBackgroundColor(disabledColor)
            return
        }

        val coin = mainActivityViewModel.coin.value
        if (coin != null && coin >= 100) {
            binding.openbtn.isEnabled = true
            binding.openbtn.setCardBackgroundColor(enabledColor)
        } else {
            binding.openbtn.isEnabled = false
            binding.openbtn.setCardBackgroundColor(disabledColor)
        }
    }



    /**
     * Float 값의 소수점 자리를 지정하여 문자열로 변환
     */
    private fun Float.format(digits: Int) = "%.${digits}f".format(this)


}