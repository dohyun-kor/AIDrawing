package com.example.gametset.room.ui.myroom

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.gametset.R
import com.example.gametset.databinding.FragmentViewMyroomBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.ItemInfo
import com.example.gametset.room.data.model.MyRoomItem
import com.example.gametset.room.data.remote.RetrofitUtil
import android.graphics.PointF
import kotlinx.coroutines.launch
import android.app.Dialog
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView

class ViewMyroomFragment : Fragment() {
    private var _binding: FragmentViewMyroomBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
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
        _binding = FragmentViewMyroomBinding.bind(view)
        
        setupUI()
        loadMyRoomData()
    }

    private fun setupUI() {
        binding.btnEditMyroom.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, EditMyroomFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadMyRoomData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                val serverItems = RetrofitUtil.myRoomService.getMyRoomItems(userId)
                
                binding.roomPreview.removeAllViews()
                serverItems.forEach { pictureDto ->
                    val frameUrl = getItemDrawableResource(pictureDto.furniture)
                    val myRoomItem = MyRoomItem.fromPictureDto(pictureDto, frameUrl)
                    displayPictureWithItem(myRoomItem)
                }
            } catch (e: Exception) {
                Log.e("ViewMyroomFragment", "Failed to load items", e)
            }
        }
    }

    private suspend fun getItemDrawableResource(itemId: Int): String {
        return try {
            val item = RetrofitUtil.storeService.getOneItem(itemId)
            if (item.link.isNotEmpty()) {
                item.link
            } else {
                "https://i12d108.p.ssafy.io/api/item/default.png"
            }
        } catch (e: Exception) {
            Log.e("ViewMyroomFragment", "Failed to get item image", e)
            "https://i12d108.p.ssafy.io/api/item/default.png"
        }
    }

    private fun displayPictureWithItem(item: MyRoomItem) {
        val containerView = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            
            val frameView = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(250, 250)
                scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(requireContext())
                    .load(item.imageUrl)
                    .into(this)
            }
            
            item.pictureUrl?.let { url ->
                val pictureView = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(200, 200).apply {
                        gravity = Gravity.CENTER
                        setMargins(25, 25, 25, 25)
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(requireContext())
                        .load(url)
                        .into(this)
                }
                addView(pictureView)
            }
            
            addView(frameView)
            x = item.position.x
            y = item.position.y
            rotation = item.rotation

            setOnClickListener {
                item.pictureUrl?.let { url ->
                    showFullScreenImage(url, item.topic ?: "")
                }
            }
        }
        
        binding.roomPreview.addView(containerView)
    }

    private fun showFullScreenImage(imageUrl: String, topic: String) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fullscreen_image)

        val imageView = dialog.findViewById<ImageView>(R.id.fullscreenImage)
        val topicText = dialog.findViewById<TextView>(R.id.imageTopicText)
        val closeButton = dialog.findViewById<ImageButton>(R.id.closeButton)

        Glide.with(requireContext())
            .load(imageUrl)
            .into(imageView)

        topicText.text = "주제: $topic"

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
        mainActivity.hideToolBar(false)
        loadMyRoomData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 