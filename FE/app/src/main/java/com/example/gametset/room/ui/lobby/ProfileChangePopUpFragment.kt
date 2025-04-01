package com.example.gametset.room.ui.lobby

import ProfileConfirmFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.gametset.R
import com.example.gametset.databinding.FragmentProfileChangePopUpBinding
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.UserProfileChangeDto



class ProfileChangePopUpFragment : DialogFragment() {
    val currentUserId = ApplicationClass.sharedPreferencesUtil.getUser().userId

    private var _binding: FragmentProfileChangePopUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileChangePopUpBinding.inflate(inflater, container, false)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LiveData 관찰
        viewModel.userProfileData.observe(viewLifecycleOwner) { user ->
            binding.nicknameText.text = user.nickname
            binding.levelText.text = user.level.toString()
            binding.coinText.text = user.point.toString()
            binding.crownText.text = user.gamesWon.toString()

            // 프로필 이미지 업데이트
            viewModel.getOneItem(user.userProfileItemId) { storeDto ->
                storeDto?.let { item ->
                    Glide.with(binding.root)
                        .load(item.link)
                        .placeholder(R.drawable.user_profile)
                        .error(R.drawable.user_profile)
                        .into(binding.profileImage)
                }
            }
        }

        // 초기 데이터 로드
        viewModel.userInfo(currentUserId)

        // 배경 클릭 시 dismiss (dialog_profile 클릭)
        binding.dialogProfile.setOnClickListener {
            dismiss()
        }

        // 프로필 본문 영역 클릭 시 이벤트 가로채기
        binding.profileMainBody.setOnClickListener {
            // 아무 동작 하지 않음 (클릭 이벤트 소비)
        }

        // X 버튼 클릭 시 dismiss
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
        // 수정하기 클릭 시 ProfileSelection Fragment 띄우기
        binding.editText.setOnClickListener {
            
            // ProfileSelectionFragment 생성 및 표시
            val profileSelectionFragment = ProfileSelectionFragment()
            profileSelectionFragment.show(
                parentFragmentManager,
                "ProfileSelectionDialog"
            )
        }

        // 디폴트 프로필로 변경하기
        binding.profileDownText.setOnClickListener {
            // 기본 프로필(itemId: 1)로 변경
            val userProfileChangeDto = UserProfileChangeDto(1)  // itemId 1은 default image
            viewModel.changeUserProfile(currentUserId, userProfileChangeDto) { success ->
                if (success) {
                    // 프로필 업데이트 성공
                    viewModel.userInfo(currentUserId)  // LiveData 업데이트
                    
                    // 변경 완료 다이얼로그 표시
                    ProfileConfirmFragment().show(
                        parentFragmentManager,
                        "ProfileConfirmDialog"
                    )

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}