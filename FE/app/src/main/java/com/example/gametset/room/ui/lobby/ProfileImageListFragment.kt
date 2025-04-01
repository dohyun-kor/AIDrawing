package com.example.gametset.room.ui.lobby

import ProfileConfirmFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.gametset.databinding.FragmentProfileImageListBinding
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gametset.room.data.model.dto.StoreDto
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.room.data.model.dto.UserProfileChangeDto

class ProfileImageListFragment : DialogFragment() {

    val currentUserId = ApplicationClass.sharedPreferencesUtil.getUser().userId
    private var _binding: FragmentProfileImageListBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainActivityViewModel by activityViewModels()
    private lateinit var adapter: ProfileImageRecyclerVIewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileImageListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        adapter = ProfileImageRecyclerVIewAdapter { itemId ->
            val userProfileChangeDto = UserProfileChangeDto(itemId)
            viewModel.changeUserProfile(currentUserId, userProfileChangeDto) { success ->
                if (success) {
                    // 프로필 업데이트 성공
                    viewModel.userInfo(currentUserId)  // LiveData 업데이트

                    // 변경 완료 다이얼로그 표시
                    ProfileConfirmFragment().show(
                        parentFragmentManager,
                        "ProfileConfirmDialog"
                    )

                    dismiss()  // 현재 다이얼로그 닫기
                }
            }
        }
        binding.profileRecyclerView.adapter = adapter
        binding.profileRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // 유저의 아이템 목록 가져오기
        viewModel.getUserItems(currentUserId) { myItems ->
            val itemIds = myItems.map { it.itemId }
            val profileItems = mutableListOf<StoreDto>()

            // 각 아이템의 상세 정보 가져오기
            itemIds.forEach { itemId ->
                viewModel.getOneItem(itemId) { storeDto ->
                    storeDto?.let { item ->
                        if (item.category == "1") {  // String으로 비교
                            profileItems.add(item)
                            adapter.submitList(profileItems)
                        }
                    }
                }
            }
        }

        // 배경 클릭 시 dismiss (dialog_profile 클릭)
        binding.dialogProfile.setOnClickListener {
            dismiss()
        }

        // 프로필 본문 영역 클릭 시 이벤트 가로채기
        binding.imageSelectorMainBody.setOnClickListener {
            // 아무 동작 하지 않음 (클릭 이벤트 소비)
        }

        // X 버튼 클릭 시 dismiss
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Grid 아이템 간격을 위한 ItemDecoration 클래스
    class GridSpaceItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount

                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }
}
