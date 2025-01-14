package com.ssafy.smartstore_jetpack.ui.side

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.data.model.dto.User
import com.ssafy.smartstore_jetpack.databinding.FragmentLiveBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class LiveFragment : Fragment() {

    private var _binding: FragmentLiveBinding? = null
    private val binding get() = _binding!!

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var user: User

    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentTime = System.currentTimeMillis()
        val liveStartTime = getLiveStartTime()
        val liveEndTime = getLiveEndTime()

        user = ApplicationClass.sharedPreferencesUtil.getUser()
        mainActivityViewModel.getUserInfo(user.id)

        if (currentTime in liveStartTime..liveEndTime) {
            // 라이브 시간이면 `on` 이미지 보이게 하고 `off`는 숨김
            binding.on.visibility = View.VISIBLE
            binding.off.visibility = View.GONE
        } else {
            // 라이브 시간이 아니면 `off` 이미지 보이게 하고 `on`은 숨김
            binding.on.visibility = View.GONE
            binding.off.visibility = View.VISIBLE
        }

        // `on` 클릭 시 이벤트 실행
        binding.on.setOnClickListener {
            if (currentTime in liveStartTime..liveEndTime) {
                // 포인트 적립 및 효과
                awardPoints()
                setupKonfettiEffect(binding.konfettiView)
            } else {
                Toast.makeText(requireContext(), "End Live Event.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.off.setOnClickListener {
            Toast.makeText(requireContext(), "End Live Event.", Toast.LENGTH_SHORT).show()

        }

    }

    private fun setupKonfettiEffect(konfettiView: KonfettiView) {
        repeat(1) {
            val randomX = Math.random()
            val randomY = Math.random()
            val party = Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xB5D5FF, 0x86CFFC, 0x0B6EFE),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                position = Position.Relative(randomX, randomY)
            )
            konfettiView.start(party)
        }
    }

    private fun awardPoints() {
        // 포인트 적립 로직
        // Retrofit 등을 사용해 서버에 요청 보내기 가능
        mainActivityViewModel.updatecoin(user.id, 1)
        Toast.makeText(requireContext(), "Earn 1 coins!", Toast.LENGTH_SHORT).show()
    }

    private fun getLiveStartTime(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 13) // 라이브 시작 시간
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getLiveEndTime(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 17) // 라이브 종료 시간
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }
        return calendar.timeInMillis
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        mainActivity.hideActionBar(false)
        mainActivity.hideBottomNav(false)
    }
}