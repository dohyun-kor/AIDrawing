package com.ssafy.smartstore_jetpack.ui.game

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.data.model.dto.User
import com.ssafy.smartstore_jetpack.databinding.FragmentCatchBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TAG = "CatchFragment_싸피"

class CatchFragment : Fragment() {

    private var binding: FragmentCatchBinding? = null


    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var user: User

    private lateinit var mainActivity: MainActivity

    private var countDownTimer: CountDownTimer? = null // 타이머 객체 관리
    private val animators = mutableListOf<ObjectAnimator>() // 모든 애니메이터를 추적

    private var alertDialog: AlertDialog? = null

    private var disabledColor: Int = 0
    private var enabledColor: Int = 0

    private var score = 0
    private val coinImages = mapOf(
        R.drawable.coin_10000 to 0.0003f, // 10000원: 0.03%
        R.drawable.coin_1000 to 0.001f,  // 1000원: 0.1%
        R.drawable.coin_500 to 0.01f,   // 500원: 1%
        R.drawable.coin_100 to 0.3f,    // 100원: 3%
        R.drawable.coin_0 to 0.9587f
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCatchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()
        user = ApplicationClass.sharedPreferencesUtil.getUser()
        mainActivityViewModel.getUserInfo(user.id)

        val lottieForeground = binding?.maincoin

        // foreground를 맨 앞으로 이동
        ViewCompat.setZ(lottieForeground!!, 2f)

        // 다이얼로그 띄우기
        initShowDialog()

        // AppBar와 BottomNav 숨김
        mainActivity.hideActionBar(true)
        mainActivity.hideBottomNav(true)

        // ColorStateList를 사용하여 활성화/비활성화 색상 정의
        enabledColor = resources.getColor(R.color.sub_theme, null) // 활성화 상태의 색상
        disabledColor = resources.getColor(R.color.disabled_color, null) // 비활성화 상태의 색상

        // 게임 시작
//            start_timer_text
        binding?.startbtn?.setOnClickListener {

            binding?.lottieBackground?.visibility = View.GONE
            binding?.maincoin?.visibility = View.GONE
            binding?.coinCardView?.visibility = View.GONE
            binding?.startbtn?.visibility = View.GONE

            object : CountDownTimer(3050, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding?.startTimerText?.visibility = View.VISIBLE
                binding?.startTimerText?.text = "${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                binding?.startTimerText?.visibility = View.GONE

                coincheck()

                startGame()
            }
        }.start()
        }

    }

    @SuppressLint("SetTextI18n")
    fun registerObserver() {
        mainActivityViewModel.coin.observe(viewLifecycleOwner) {
            binding?.mycoinBox?.text = "${it} coins"
            coincheck()
        }
    }


    fun coincheck() {

        val coin = mainActivityViewModel.coin.value
        if (coin != null && coin >= 100) {
            binding?.startbtn?.isEnabled = true
            binding?.startbtn?.setCardBackgroundColor(enabledColor)
        } else {
            binding?.startbtn?.isEnabled = false
            binding?.startbtn?.setCardBackgroundColor(disabledColor)
        }
    }


    fun initEvent() {
        // 결과를 다이얼로그로 표시
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Congratulations!!")
            .setMessage("Earn $score coins!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        // 다이얼로그를 밖을 터치하거나 뒤로가기 버튼으로 닫히지 않게 설정
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.show()

        // coinAmount 만큼 코인 업데이트 (ViewModel을 통해 서버에 반영하는 부분은 생략)
        mainActivityViewModel.updatecoin(user.id, score)
    }





//fun initShowDialog(){
//        // AlertDialog 생성
//        val inflater = LayoutInflater.from(requireContext())
//        val dialogView = inflater.inflate(R.layout.dialog_probability, null)
//
//
//        val alertDialog = AlertDialog.Builder(requireContext())
//            .setView(dialogView)
//            .setPositiveButton("OK") { dialog, _ ->
//                dialog.dismiss()
//
//            }
//            .create()
//
//        // 다이얼로그 표시
//        alertDialog.show()
//    }
    fun initShowDialog() {
        // 이미 표시 중인 Dialog가 있으면 새로 생성하지 않음
        if (alertDialog != null && alertDialog!!.isShowing) {
            return
        }

        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_probability, null)

        alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog?.show()
    }


    private fun startGame() {

        // 게임 시작시 뷰모델 코인 레트로핏 연결
        mainActivityViewModel.updatecoin(user.id, -100)

        score = 0
        binding?.scoreText?.text = "Coin: $score"

        // 제한 시간 타이머 시작 (10초)
        countDownTimer = object : CountDownTimer(10050, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding?.timerText?.text = "Time: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                binding?.timerText?.text = "Game Over!"
                // 게임 종료 로직
                gameOver()
            }
        }.start()

        // 1초마다 코인 생성
        val timer = object : CountDownTimer(10000, 200) {
            override fun onTick(millisUntilFinished: Long) {
                spawnCoin()
            }

            override fun onFinish() {
                // 게임 종료 시 타이머 정지
            }
        }
        timer.start()
    }

    private fun spawnCoin() {
        val containerWidth = binding?.bugContainer?.width ?: return
        val containerHeight = binding?.bugContainer?.height ?: return

        if (containerWidth > 0 && containerHeight > 0) {
            val coinResource = pickRandomCoin()
            val coinView = ImageView(requireContext()).apply {
                setImageResource(coinResource.first)
                layoutParams = FrameLayout.LayoutParams(170, 170) // 코인 크기
            }

            binding?.bugContainer?.addView(coinView)

            val coinWidth = 170
            val startX = Random.nextInt(containerWidth - coinWidth)
            coinView.x = startX.toFloat()
            coinView.y = -coinWidth.toFloat()

            val endY = containerHeight.toFloat()

            val animatorY = ObjectAnimator.ofFloat(coinView, "y", -coinWidth.toFloat(), endY)
            animatorY.duration = Random.nextLong(1000, 1200)
            animatorY.start()

            // 애니메이터를 관리 리스트에 추가
            animators.add(animatorY)

            // 코인 클릭 이벤트
            coinView.setOnClickListener {
                score += coinResource.second
                binding?.scoreText?.text = "Score: $score"
                binding?.bugContainer?.removeView(coinView) // 코인 제거
            }

            // 화면 밖으로 나가면 제거
            animatorY.addUpdateListener {
                if (coinView.y >= containerHeight) {
                    binding?.bugContainer?.removeView(coinView)
                }
            }
        } else {
            Log.e(TAG, "Container width or height is zero")
        }
    }

    private fun pickRandomCoin(): Pair<Int, Int> {
        val randomValue = Random.nextFloat()
        var cumulativeProbability = 0f
        for ((coin, probability) in coinImages) {
            cumulativeProbability += probability
            if (randomValue <= cumulativeProbability) {
                return when (coin) {
                    R.drawable.coin_10000 -> Pair(coin, 10000)
                    R.drawable.coin_1000 -> Pair(coin, 1000)
                    R.drawable.coin_500 -> Pair(coin, 500)
                    R.drawable.coin_100 -> Pair(coin, 100)
                    R.drawable.coin_0 -> Pair(coin, 0)
                    else -> Pair(coin, 0)
                }
            }
        }
        return Pair(R.drawable.coin_0, 0) // 기본값
    }

    private fun gameOver() {
        Toast.makeText(requireContext(), "Earn $score Points!", Toast.LENGTH_SHORT).show()
        // 추가적으로 다이얼로그로 결과 표시 가능
        lifecycleScope.launch {
            delay(1000)
            initEvent()
        }

        coincheck()

        binding?.lottieBackground?.visibility = View.VISIBLE
        binding?.startbtn?.visibility = View.VISIBLE
        binding?.coinCardView?.visibility = View.VISIBLE
        binding?.maincoin?.visibility = View.VISIBLE


    }

    override fun onPause() {
        super.onPause()
        // 애니메이터 정지 및 초기화
        animators.forEach {
            it.cancel()
            it.removeAllListeners() // 모든 리스너 제거
        }
        animators.clear()

        //뒤로 가기 시 타이머 뷰 해제
        countDownTimer?.cancel()
        countDownTimer = null // 타이머 해제

    }

    override fun onStop() {
        super.onStop()

        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
        alertDialog?.dismiss()
        alertDialog = null

        // AppBar와 BottomNav 다시 표시
        mainActivity.hideActionBar(false)
        mainActivity.hideBottomNav(false)
    }


}
