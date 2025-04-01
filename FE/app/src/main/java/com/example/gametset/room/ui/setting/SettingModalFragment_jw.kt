package com.example.gametset.room.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.example.gametset.R
import com.example.gametset.databinding.FragmentSettingModalJwBinding
import com.example.gametset.room.util.SharedPreferencesUtil

class SettingModalFragment_jw : DialogFragment() {
    private var _binding: FragmentSettingModalJwBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingModalJwBinding.inflate(inflater, container, false)
        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
        
        setupSettings()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun setupSettings() {
        // BGM 설정
        updateBgmIcon(sharedPreferencesUtil.getBgmVolume() > 0)
        binding.bgmButton.setOnClickListener {
            val currentVolume = sharedPreferencesUtil.getBgmVolume()
            val newVolume = if (currentVolume > 0) 0 else 100
            sharedPreferencesUtil.saveBgmVolume(newVolume)
            updateBgmIcon(newVolume > 0)
        }

        // 효과음 설정
        updateSfxIcon(sharedPreferencesUtil.getSfxVolume() > 0)
        binding.sfxButton.setOnClickListener {
            val currentVolume = sharedPreferencesUtil.getSfxVolume()
            val newVolume = if (currentVolume > 0) 0 else 100
            sharedPreferencesUtil.saveSfxVolume(newVolume)
            updateSfxIcon(newVolume > 0)
        }

        // 진동 설정
        updateVibrationIcon(sharedPreferencesUtil.getVibrationEnabled())
        binding.vibrationButton.setOnClickListener {
            val currentState = sharedPreferencesUtil.getVibrationEnabled()
            sharedPreferencesUtil.saveVibrationEnabled(!currentState)
            updateVibrationIcon(!currentState)
        }

        // 닫기 버튼 설정
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun updateBgmIcon(isOn: Boolean) {
        if (isOn) {
            binding.bgmButton.setImageResource(R.drawable.setting_background_sound_sw)
            binding.bgmButton.background = null
        } else {
            binding.bgmButton.setImageResource(R.drawable.setting_background_sound_sw)
            binding.bgmButton.setBackgroundResource(R.drawable.setting_off_overlay_jw)
        }
    }

    private fun updateSfxIcon(isOn: Boolean) {
        if (isOn) {
            binding.sfxButton.setImageResource(R.drawable.setting_sound_effect_sw)
            binding.sfxButton.background = null
        } else {
            binding.sfxButton.setImageResource(R.drawable.setting_sound_effect_sw)
            binding.sfxButton.setBackgroundResource(R.drawable.setting_off_overlay_jw)
        }
    }

    private fun updateVibrationIcon(isOn: Boolean) {
        if (isOn) {
            binding.vibrationButton.setImageResource(R.drawable.setting_vibration)
            binding.vibrationButton.background = null
        } else {
            binding.vibrationButton.setImageResource(R.drawable.setting_vibration)
            binding.vibrationButton.setBackgroundResource(R.drawable.setting_off_overlay_jw)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 