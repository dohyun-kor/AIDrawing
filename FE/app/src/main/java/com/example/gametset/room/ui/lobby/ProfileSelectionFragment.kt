package com.example.gametset.room.ui.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gametset.databinding.FragmentProfileSelectionBinding

class ProfileSelectionFragment : DialogFragment() {

    private var _binding: FragmentProfileSelectionBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileSelectionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 배경 클릭 시 dismiss (dialog_profile 클릭)
        binding.dialogProfile.setOnClickListener {
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

        binding.profileText.setOnClickListener {
            dismiss()

            // ProfileSelectionFragment 생성 및 표시
            val profileImageListFragment = ProfileImageListFragment()
            profileImageListFragment.show(
                parentFragmentManager,
                "ProfileSelectionDialog"
            )
        }

        binding.nicknameText.setOnClickListener{
            dismiss()

            val profileNicknameChangeFragment = ProfileNicknameChangeFragment()
            profileNicknameChangeFragment.show(
                parentFragmentManager,
                "NicknameChangeDialog"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}