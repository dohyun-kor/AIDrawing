package com.example.gametset.room.ui.myroom

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.gametset.R
import com.example.gametset.databinding.FragmentViewMyroomBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.data.model.dto.PictureDto
import android.view.Gravity
import kotlinx.coroutines.launch
import com.example.gametset.room.data.remote.RetrofitUtil
import androidx.lifecycle.lifecycleScope
import android.app.Dialog
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView

class FriendMyroomFragment_jw : Fragment() {
    private var _binding: FragmentViewMyroomBinding? = null
    private val binding get() = _binding!!
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

    private var friendId: Int = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            friendId = it.getInt("friendId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewMyroomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 편집 버튼을 뒤로가기 버튼으로 변경
        binding.btnEditMyroom.apply {
            text = "뒤로가기"
            icon = resources.getDrawable(R.drawable.back_btn_sw, null)
            visibility = View.VISIBLE
            setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        // 친구의 마이룸 데이터 로드
        if (friendId != -1) {
            Log.d("FriendMyroomFragment", "Loading friend's myroom: friendId=$friendId")
            mainActivityViewModel.getFriendMyRoom(friendId)

            // 데이터 관찰
            mainActivityViewModel.friendMyRoomItems.observe(viewLifecycleOwner) { items ->
                Log.d("FriendMyroomFragment", "Received ${items.size} items")
                binding.roomPreview.removeAllViews() // 기존 뷰 제거
                
                items.forEach { item ->
                    Log.d("FriendMyroomFragment", "Adding item: $item")
                    val itemView = createItemView(item)
                    itemView.apply {
                        x = item.xval
                        y = item.yval
                        rotation = item.rotation.toFloat()
                    }
                    binding.roomPreview.addView(itemView)
                }
            }
        }
    }

    private fun createItemView(item: PictureDto): View {
        return FrameLayout(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(300, 300)
            
            // 그림 이미지를 먼저 추가
            val pictureView = ImageView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(250, 250).apply {
                    gravity = Gravity.CENTER
                    setMargins(25, 25, 25, 25)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                tag = "picture"
            }
            addView(pictureView)
            
            // 그림 이미지 로드
            Glide.with(requireContext())
                .load(item.imageUrl)
                .into(pictureView)
            
            // 액자 이미지를 나중에 추가
            val frameView = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(300, 300)
                scaleType = ImageView.ScaleType.FIT_XY
                tag = "frame"
            }
            addView(frameView)
            
            // 액자 이미지 로드
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val frameUrl = RetrofitUtil.storeService.getOneItem(item.furniture).link
                    Glide.with(requireContext())
                        .load(frameUrl)
                        .override(300, 300)
                        .placeholder(R.drawable.store_item_box_js)
                        .error(R.drawable.store_item_box_js)
                        .into(frameView)
                } catch (e: Exception) {
                    Log.e("FriendMyroomFragment", "Failed to load frame image", e)
                }
            }

            // 클릭 리스너 수정
            setOnClickListener {
                showFullScreenImage(item.imageUrl, item.topic)
            }
        }
    }

    private fun showFullScreenImage(imageUrl: String, topic: String) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fullscreen_image)

        val imageView = dialog.findViewById<ImageView>(R.id.fullscreenImage)
        val topicText = dialog.findViewById<TextView>(R.id.imageTopicText)
        val closeButton = dialog.findViewById<ImageButton>(R.id.closeButton)

        // 이미지 로드
        Glide.with(requireContext())
            .load(imageUrl)
            .into(imageView)

        // topic 설정
        topicText.text = "주제: $topic"

        // 닫기 버튼 클릭 리스너
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // 이미지 클릭 시에도 다이얼로그 닫기
        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(friendId: Int) = FriendMyroomFragment_jw().apply {
            arguments = Bundle().apply {
                putInt("friendId", friendId)
            }
        }
    }
} 