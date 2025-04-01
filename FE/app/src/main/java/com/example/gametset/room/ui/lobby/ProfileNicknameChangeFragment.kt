package com.example.gametset.room.ui.lobby

import ProfileConfirmFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.gametset.databinding.FragmentProfileNicknameChangeBinding
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.remote.RetrofitUtil


class ProfileNicknameChangeFragment : DialogFragment() {

    val currentUserId = ApplicationClass.sharedPreferencesUtil.getUser().userId
    private var _binding: FragmentProfileNicknameChangeBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainActivityViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileNicknameChangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 배경 클릭 시 dismiss (dialog_profile 클릭)
        binding.nicknameDialog.setOnClickListener {
            dismiss()
        }

        // 프로필 본문 영역 클릭 시 이벤트 가로채기
        binding.selectorMainBody.setOnClickListener {
            // 아무 동작 하지 않음 (클릭 이벤트 소비)
        }

        // X 버튼 클릭 시 dismiss
        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        binding.confirmButton.setOnClickListener{
           nicknameChange()
        }


    }

    fun nicknameChange() {
        val newNickname = binding.nicknameEditText.text.toString()
        
        viewModel.checkAndUpdateNickname(currentUserId, newNickname) { errorMessage ->
            errorMessage?.let { 
                // 에러가 있는 경우
                binding.nicknameEditText.error = it
            } ?: run {
                // 성공한 경우
                ApplicationClass.sharedPreferencesUtil.localUpdateNickname(newNickname)
                ProfileConfirmFragment().show(
                    parentFragmentManager,
                    "ProfileConfirmDialog"
                )
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}