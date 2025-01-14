package com.ssafy.smartstore_jetpack.ui.game

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.data.model.dto.User
import com.ssafy.smartstore_jetpack.databinding.FragmentRouletteBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel

private const val TAG = "RouletteFragment_싸피"

class RouletteFragment : Fragment() {

    private var _binding: FragmentRouletteBinding? = null
    private val binding get() = _binding!!

    private var mainActivity: MainActivity? = null
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var user: User
    private var isPlaying = false

    private var disabledColor: Int = 0
    private var enabledColor: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouletteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()
        user = ApplicationClass.sharedPreferencesUtil.getUser()
        mainActivityViewModel.getUserInfo(user.id)

        val lottieForeground = binding.rouletteView

        // foreground를 맨 앞으로 이동
        ViewCompat.setZ(lottieForeground, 2f)


        // AppBar와 BottomNav 숨김
        mainActivity?.hideActionBar(true)
        mainActivity?.hideBottomNav(true)

        // 확률 다이얼로그 표시
        showProbabilitiesDialog()

        // 룰렛 데이터와 확률 설정
        val dataList =
            listOf("+1000 coin", "+10000 coin", "Nothing", "+500 coin", "+100 coin", "Nothing")
        val colors = listOf(
            Color.parseColor("#FFB3BA"), // 파스텔 핑크
            Color.parseColor("#FFDFBA"), // 파스텔 오렌지
            Color.parseColor("#FFFFBA"), // 파스텔 옐로우
            Color.parseColor("#BAFFC9"), // 파스텔 민트
            Color.parseColor("#BAE1FF"), // 파스텔 블루
            Color.parseColor("#E6A8D7") // 파스텔 라벤더
        )
//        val probabilities = listOf(0.3f, 0.2f, 0.4f, 0.02f, 0.08f) // 각 구역의 확률 (0~1 사이 합이 1이어야 함)
//        val probabilities = listOf(1.0f, 0.0f, 0.0f, 0.00f, 0.00f) // 각 구역의 확률 (0~1 사이 합이 1이어야 함)
        binding.rouletteView.setRouletteData(dataList, colors) { result ->
            // 룰렛 회전 완료 후 결과 처리
            showResultDialog(result)
        }

        // ColorStateList를 사용하여 활성화/비활성화 색상 정의
        enabledColor = resources.getColor(R.color.sub_theme, null) // 활성화 상태의 색상
        disabledColor = resources.getColor(R.color.disabled_color, null) // 비활성화 상태의 색상

        // 버튼 클릭 시 룰렛 회전
        binding.spinButton.setOnClickListener {
            binding.spinButton.isEnabled = false
            isPlaying = true
            mainActivityViewModel.updatecoin(user.id, -100)
            val randomDegrees = calculateRandomDegrees(probabilities)
            binding.rouletteView.rotateRouletteByProbability(randomDegrees)
        }
    }



    /**
     * 확률 기반으로 랜덤 각도 계산
     */
    private fun calculateRandomDegrees(probabilities: List<Float>): Float {
        val randomOffset = (0..360).random() // 구역 내 랜덤 각도
        return ((360 * 7) + randomOffset).toFloat() // 7회전 + 선택된 구역 + 구역 내 랜덤 각도
    }


    private fun showResultDialog(result: String) {
        // 결과를 다이얼로그로 표시
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Congratulations!!")
            .setMessage("The selected item: $result")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
        when (result) {
            "+100 coin" -> {
                mainActivityViewModel.updatecoin(user.id, 100)
            }

            "+500 coin" -> {
                mainActivityViewModel.updatecoin(user.id, 500)
            }

            "+1000 coin" -> {
                mainActivityViewModel.updatecoin(user.id, 1000)
            }

            "+10000 coin" -> {
                mainActivityViewModel.updatecoin(user.id, 10000)
            }

            else -> {

            }
        }
        dialog.show()
        isPlaying = false
        coincheck()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // AppBar와 BottomNav 다시 표시
        mainActivity?.hideActionBar(false)
        mainActivity?.hideBottomNav(false)
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }

    @SuppressLint("SetTextI18n")
    fun registerObserver() {
        mainActivityViewModel.coin.observe(viewLifecycleOwner) {
            binding.mycoinRoulette.text = "${it} coins"
            coincheck()
        }
    }

     fun coincheck() {
         if (isPlaying) {
             // 박스가 열리는 중이면 버튼 비활성화
             binding.spinButton.isEnabled = false
             binding.spinButton.setCardBackgroundColor(disabledColor)
             return
         }

         val coin = mainActivityViewModel.coin.value
         if (coin != null && coin >= 100) {
             binding.spinButton.isEnabled = true
             binding.spinButton.setCardBackgroundColor(enabledColor)
         } else {
             binding.spinButton.isEnabled = false
             binding.spinButton.setCardBackgroundColor(disabledColor)
         }
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



    /**
     * Float 값의 소수점 자리를 지정하여 문자열로 변환
     */
    private fun Float.format(digits: Int) = "%.${digits}f".format(this)

    companion object {
        val probabilities = listOf(0.025f, 0.005f, 0.4f, 0.05f, 0.2f, 0.32f)
    }
}
