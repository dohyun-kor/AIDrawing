package com.example.gametset.room.ui.myroom

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gametset.R
import com.example.gametset.databinding.FragmentEditMyroomBinding
import com.example.gametset.room.MainActivity
import android.graphics.PointF
import android.widget.TextView
import android.widget.PopupWindow
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import android.widget.RatingBar
import android.widget.ImageButton
import com.example.gametset.room.data.model.ItemInfo
import com.example.gametset.room.data.model.GuestBook
import com.example.gametset.room.data.model.MyRoomItem
import com.example.gametset.room.base.ApplicationClass
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.widget.GridLayout
import com.bumptech.glide.Glide
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.data.model.dto.MyItemDto
import com.example.gametset.room.data.model.response.PictureDisplayRequestDto
import com.example.gametset.room.data.model.dto.PictureDto
import android.app.Dialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


/**
 * A simple [Fragment] subclass.
 * Use the [MyroomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditMyroomFragment : Fragment() {

    private var _binding: FragmentEditMyroomBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private var currentCategory = "2" // 현재 선택된 카테고리
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetBinding: View
    private var selectedItem: MyRoomItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditMyroomBinding.inflate(inflater, container, false)
        
        // BottomSheet 초기화를 먼저 수행
        bottomSheetBinding = binding.bottomSheet.root
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBinding)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        
        // 초기화 후에 setup 메서드들 호출
        setupItemSelection()
        setupPictureSelection()
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바의 메뉴 버튼을 뒤로가기 버튼으로 변경
        val toolbarMenuBtn = mainActivity.binding.toolbar.toolBarMenuBtn
        toolbarMenuBtn.setImageResource(R.drawable.back_btn_sw)
        
        // 뒤로가기 버튼 클릭 이벤트 처리
        toolbarMenuBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // 프래그먼트가 파괴될 때 원래 메뉴 버튼으로 복원하기 위해 리스너 등록
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                toolbarMenuBtn.setImageResource(R.drawable.toolbar_menu_sw)
                // 원래 메뉴 버튼의 클릭 리스너 복원
                toolbarMenuBtn.setOnClickListener {
                    if (!mainActivity.isOpenMenu) {
                        mainActivity.isOpenMenu = true
                        mainActivity.openFragment(3)
                    } else {
                        mainActivity.isOpenMenu = false
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        })

        // 아이템 추가 버튼 클릭 리스너
        binding.btnEditRoom.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        // 전체 저장 버튼 클릭 리스너
        binding.btnSaveAll.setOnClickListener {
            saveAllItems()
        }

        // 앨범 버튼 클릭 이벤트
        binding.albumButtonJw.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .add(android.R.id.content, DrawingAlbumFragment_jw.newInstance())
                .addToBackStack(null)
                .commit()
        }

        setupCategoryListeners()
        loadSavedItems()
    }

    private fun setupCategoryListeners() {
        // 카테고리 버튼이 제거되었으므로 바로 액자 카테고리 로드
        loadItemsByCategory("2")
    }

    private fun loadItemsByCategory(category: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                val myItems = RetrofitUtil.myItemService.myItemsList(userId)
                val filteredItems = myItems.filter { it.category == category }
                
                // RecyclerView 설정
                val recyclerView = bottomSheetBinding.findViewById<RecyclerView>(R.id.itemGrid)
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 3열 그리드
                recyclerView.adapter = ItemGridAdapter(filteredItems) { myItem ->
                    addItemToRoom(myItem)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                
            } catch (e: Exception) {
                Log.e("EditMyroomFragment", "Failed to load my items", e)
                Toast.makeText(context, "내 아이템을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addItemToRoom(myItem: MyItemDto) {
        viewLifecycleOwner.lifecycleScope.launch {
            val frameUrl = getItemDrawableResource(myItem.itemId)
            val newItem = MyRoomItem(
                id = myItem.itemId,
                type = "FRAME",
                imageUrl = frameUrl,
                position = PointF(binding.roomCanvas.width / 2f, binding.roomCanvas.height / 2f)
            )
            addItemToView(newItem)
        }
    }

    private fun createTouchListener(): View.OnTouchListener {
        return object : View.OnTouchListener {
            private var lastAction = 0
            private var isDragging = false
            private val touchPoint = PointF()
            private var startX = 0f
            private var startY = 0f
            private val CLICK_THRESHOLD = 10f // 클릭으로 인정할 이동 거리 임계값
            
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastAction = MotionEvent.ACTION_DOWN
                        isDragging = false
                        touchPoint.set(event.rawX - v.x, event.rawY - v.y)
                        startX = event.rawX
                        startY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = Math.abs(event.rawX - startX)
                        val dy = Math.abs(event.rawY - startY)
                        
                        // 일정 거리 이상 이동했을 때만 드래그로 인식
                        if (dx > CLICK_THRESHOLD || dy > CLICK_THRESHOLD) {
                            isDragging = true
                            v.x = event.rawX - touchPoint.x
                            v.y = event.rawY - touchPoint.y
                            
                            // 위치 업데이트
                            (v.tag as? MyRoomItem)?.let { item ->
                                val updatedItem = item.copy(
                                    position = PointF(v.x, v.y)
                                )
                                v.tag = updatedItem
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isDragging) {
                            // 드래그하지 않았을 경우에만 팝업 메뉴 표시
                            val dx = Math.abs(event.rawX - startX)
                            val dy = Math.abs(event.rawY - startY)
                            if (dx < CLICK_THRESHOLD && dy < CLICK_THRESHOLD) {
                                showItemPopupMenu(v)
                            }
                        } else {
                            // 드래그 종료 시 서버에 저장
                            (v.tag as? MyRoomItem)?.let { item ->
                                savePictureToServer(item)
                            }
                        }
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun showItemPopupMenu(view: View) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_item_menu, null)
        
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // 그림 추가 버튼 클릭 리스너
        popupView.findViewById<TextView>(R.id.btn_add_drawing).setOnClickListener {
            popupWindow.dismiss()
            
            // DrawingAlbumFragment 호출
            val drawingAlbumFragment = DrawingAlbumFragment_jw.newInstance()
            drawingAlbumFragment.show(childFragmentManager, "drawing_album")
            
            // 그림 선택 결과 처리
            drawingAlbumFragment.setOnPictureSelectedListener(object : DrawingAlbumFragment_jw.OnPictureSelectedListener {
                override fun onPictureSelected(pictureDto: PictureDto) {
                    // 현재 선택된 프레임 아이템 가져오기
                    val frameItem = view.tag as? MyRoomItem ?: return
                    
                    // 기존 프레임에 그림 적용
                    val updatedItem = frameItem.copy(
                        pictureId = pictureDto.pictureId,
                        pictureUrl = pictureDto.imageUrl
                    )
                    
                    // 컨테이너 뷰 업데이트
                    val containerView = view as? FrameLayout ?: return
                    
                    // 기존 그림이 있다면 제거
                    for (i in 0 until containerView.childCount) {
                        val child = containerView.getChildAt(i)
                        if (child is ImageView && child.tag == "picture") {
                            containerView.removeView(child)
                            break
                        }
                    }
                    
                    // 새 그림 추가
                    val pictureView = ImageView(requireContext()).apply {
                        layoutParams = FrameLayout.LayoutParams(200, 200).apply {
                            gravity = Gravity.CENTER
                            setMargins(25, 25, 25, 25)
                        }
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        tag = "picture"
                        Glide.with(requireContext())
                            .load(pictureDto.imageUrl)
                            .into(this)
                    }
                    containerView.addView(pictureView)
                    
                    // 태그 업데이트
                    containerView.tag = updatedItem
                    
                    // 서버에 저장
                    savePictureToServer(updatedItem)
                }
            })
        }

        // 상세정보 버튼 클릭 이벤트
        popupView.findViewById<TextView>(R.id.btn_info).setOnClickListener {
            showItemInfoPopup(view)
            popupWindow.dismiss()
        }



        // 회전 버튼 클릭 이벤트
        popupView.findViewById<TextView>(R.id.btn_rotate).setOnClickListener {
            view.rotation = (view.rotation + 90) % 360
            // 회전 후 tag 업데이트
            (view.tag as? MyRoomItem)?.let { item ->
                view.tag = item.copy(rotation = view.rotation)
            }
            popupWindow.dismiss()
        }

        // 삭제 버튼 클릭 이벤트
        popupView.findViewById<TextView>(R.id.btn_delete).setOnClickListener {
            deleteItem(view)
            popupWindow.dismiss()
        }



        popupWindow.showAsDropDown(view)
    }

    private fun saveItemPosition(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val myRoomItem = (view.tag as? MyRoomItem)?.apply {
                    position = PointF(view.x, view.y)
                    rotation = view.rotation
                } ?: return@launch
                
                val displayRequests = listOf(myRoomItem.toDisplayRequest())
                
                val response = RetrofitUtil.myRoomService.saveMyRoomItems(
                    ApplicationClass.sharedPreferencesUtil.getUser().userId,
                    displayRequests
                )

                if (response.isSuccessful) {
                    // 현재 아이템 목록 업데이트
                    val currentItems = ApplicationClass.sharedPreferencesUtil.getMyRoomItems().toMutableList()
                    val index = currentItems.indexOfFirst { it.id == myRoomItem.id }
                    if (index != -1) {
                        currentItems[index] = myRoomItem
                    } else {
                        currentItems.add(myRoomItem)
                    }
                    ApplicationClass.sharedPreferencesUtil.saveMyRoomItems(currentItems)
                    
                    Toast.makeText(context, "위치가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("EditMyroomFragment", "Failed to save item position", e)
            }
        }
    }

    private fun showItemGuestBookPopup(itemView: View) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_guestbook, null)
        
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // 이전에 저장된 정보 불러오기
        val tag = itemView.tag as? ItemInfo ?: ItemInfo("", "", "", "")
        if (itemView.tag == null) itemView.tag = tag

        // RecyclerView 설정
        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recycler_guestbook)
        
        fun updateAdapter(guestBooks: List<GuestBook>) {
            recyclerView.adapter = GuestBookAdapter(guestBooks) { updatedGuestBooks ->
                itemView.tag = tag.updateGuestBooks(updatedGuestBooks)
                updateAdapter(updatedGuestBooks)
            }
        }

        updateAdapter(tag.guestBooks)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 등록 버튼 클릭 리스너
        popupView.findViewById<Button>(R.id.btn_submit).setOnClickListener {
            val message = popupView.findViewById<EditText>(R.id.edit_message).text.toString()
            val rating = popupView.findViewById<RatingBar>(R.id.rating_bar).rating

            if (message.isNotEmpty()) {
                val guestBook = GuestBook(
                    visitorName = "방문자", // TODO: 실제 사용자 이름으로 변경
                    message = message,
                    rating = rating
                )
                
                val currentTag = itemView.tag as? ItemInfo ?: tag
                val newGuestBooks = listOf(guestBook) + currentTag.guestBooks
                itemView.tag = currentTag.updateGuestBooks(newGuestBooks)
                updateAdapter(newGuestBooks)
                
                popupView.findViewById<EditText>(R.id.edit_message).text.clear()
                popupView.findViewById<RatingBar>(R.id.rating_bar).rating = 0f
                Toast.makeText(context, "방명록이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        popupView.findViewById<Button>(R.id.btn_close).setOnClickListener {
            popupWindow.dismiss()
        }

        popupView.findViewById<Button>(R.id.btn_close).setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.showAtLocation(
            binding.root,
            Gravity.CENTER,
            0,
            0
        )
    }

    private fun showItemInfoPopup(itemView: View) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.popup_item_info)
        
        // 이전에 저장된 정보 불러오기
        val tag = itemView.tag as? ItemInfo
        val myRoomItem = itemView.tag as? MyRoomItem
        
        dialog.apply {
            findViewById<ImageView>(R.id.image_item)?.let { imageItem ->
                when (itemView) {
                    is ImageView -> imageItem.setImageDrawable(itemView.drawable)
                    is ViewGroup -> {
                        itemView.findViewById<ImageView>(android.R.id.icon)?.let { 
                            imageItem.setImageDrawable(it.drawable)
                        }
                    }
                    else -> imageItem.setImageDrawable(null)
                }
            }

            findViewById<TextView>(R.id.text_artist_name)?.text = tag?.artistName ?: "정보 없음"
            findViewById<TextView>(R.id.text_artwork_name)?.text = tag?.artworkName ?: "정보 없음"
            findViewById<TextView>(R.id.text_artwork_type)?.text = tag?.artworkType ?: "정보 없음"
            findViewById<TextView>(R.id.text_artwork_description)?.text = tag?.description ?: "정보 없음"

            findViewById<Button>(R.id.btn_add_picture)?.apply {
                visibility = if (myRoomItem?.pictureUrl != null) View.VISIBLE else View.GONE
                
                setOnClickListener {
                    val albumFragment = DrawingAlbumFragment_jw.newInstance()
                    albumFragment.setOnPictureSelectedListener(object : DrawingAlbumFragment_jw.OnPictureSelectedListener {
                        override fun onPictureSelected(pictureDto: PictureDto) {
                            if (myRoomItem != null) {
                                applyPictureToFrame(myRoomItem, pictureDto)
                            }
                            dialog.dismiss()
                        }
                    })
                    albumFragment.show(childFragmentManager, "album")
                }
            }

            findViewById<Button>(R.id.btn_close)?.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showItemInfoEditPopup(itemView: View) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_item_info, null)
        
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // 이전에 저장된 정보 불러오기
        val tag = itemView.tag as? ItemInfo
        
        // 아이템 정보 설정
        popupView.apply {
            // ImageView 가져오기 수정
            val imageItem = findViewById<ImageView>(R.id.image_item)
            when (itemView) {
                is ImageView -> imageItem.setImageDrawable(itemView.drawable)
                is ViewGroup -> {
                    val imageView = itemView.findViewById<ImageView>(android.R.id.icon)
                    imageView?.let { imageItem.setImageDrawable(it.drawable) }
                }
            }
            
            // 저장된 정보가 있으면 표시
            findViewById<EditText>(R.id.edit_artist_name).setText(tag?.artistName ?: "")
            findViewById<EditText>(R.id.edit_artwork_name).setText(tag?.artworkName ?: "")
            findViewById<EditText>(R.id.edit_artwork_type).setText(tag?.artworkType ?: "")
            findViewById<EditText>(R.id.edit_artwork_description).setText(tag?.description ?: "")

            // 저장 버튼 클릭 이벤트
            findViewById<Button>(R.id.btn_save).setOnClickListener {
                val newInfo = ItemInfo(
                    artistName = findViewById<EditText>(R.id.edit_artist_name).text.toString(),
                    artworkName = findViewById<EditText>(R.id.edit_artwork_name).text.toString(),
                    artworkType = findViewById<EditText>(R.id.edit_artwork_type).text.toString(),
                    description = findViewById<EditText>(R.id.edit_artwork_description).text.toString(),
                    guestBooks = tag?.guestBooks ?: listOf()
                )
                itemView.tag = newInfo
                Toast.makeText(context, "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
                // 저장 후 조회 팝업 다시 표시
                showItemInfoPopup(itemView)
            }

            findViewById<Button>(R.id.btn_close).setOnClickListener {
                popupWindow.dismiss()
                // 취소 시 조회 팝업 다시 표시
                showItemInfoPopup(itemView)
            }
        }

        popupWindow.showAtLocation(
            binding.root,
            Gravity.CENTER,
            0,
            0
        )
    }

    private suspend fun getItemDrawableResource(itemId: Int): String {
        return try {
            val item = RetrofitUtil.storeService.getOneItem(itemId)
            if (item.link.isNotEmpty()) {
                item.link
            } else {
                // 기본 이미지 URL 반환
                "https://i12d108.p.ssafy.io/api/item/default.png"
            }
        } catch (e: Exception) {
            Log.e("EditMyroomFragment", "Failed to get item image", e)
            // 에러 발생 시 기본 이미지 URL 반환
            "https://i12d108.p.ssafy.io/api/item/default.png"
        }
    }

    // 저장된 아이템들을 불러와서 표시하는 메서드 추가
    private fun loadSavedItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                val serverItems = RetrofitUtil.myRoomService.getMyRoomItems(userId)
                
                binding.roomCanvas.removeAllViews()
                serverItems.forEach { pictureDto ->
                    val frameUrl = getItemDrawableResource(pictureDto.furniture)
                    val myRoomItem = MyRoomItem.fromPictureDto(pictureDto, frameUrl)
                    displayPictureWithItem(myRoomItem)
                }
            } catch (e: Exception) {
                Log.e("EditMyroomFragment", "Failed to load items", e)
            }
        }
    }

    private fun addItemToView(item: MyRoomItem) {
        val containerView = FrameLayout(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(300, 300)
            x = item.position.x
            y = item.position.y
            rotation = item.rotation
            tag = item
            
            // 액자 이미지 추가
            val frameView = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(300, 300)
                scaleType = ImageView.ScaleType.FIT_XY
                tag = "frame"
                
                Glide.with(requireContext())
                    .load(item.imageUrl)
                    .override(300, 300)
                    .placeholder(R.drawable.store_item_box_js)
                    .error(R.drawable.store_item_box_js)
                    .into(this)
            }
            addView(frameView)
            
            // 기존 그림이 있다면 추가
            item.pictureUrl?.let { url ->
                val pictureView = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(250, 250).apply {
                        gravity = Gravity.CENTER
                        setMargins(25, 25, 25, 25)
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    tag = "picture"
                    
                    Glide.with(requireContext())
                        .load(url)
                        .into(this)
                }
                addView(pictureView)
            }
            
            setOnTouchListener(createTouchListener())
        }
        
        binding.roomCanvas.addView(containerView)
    }

    // 아이템이 삭제될 때 SharedPreferences에서도 삭제하는 메서드 추가
    private fun deleteItem(view: View) {
        (view.tag as? MyRoomItem)?.let { item ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // 서버에서 아이템 삭제 API 호출
                    val response = RetrofitUtil.myRoomService.deleteMyRoomItem(
                        ApplicationClass.sharedPreferencesUtil.getUser().userId,
                        item.id
                    )
                    if (response.isSuccessful) {
                        binding.roomCanvas.removeView(view)
                    }
                } catch (e: Exception) {
                    Log.e("EditMyroomFragment", "Failed to delete item", e)
                }
            }
        }
        binding.roomCanvas.removeView(view)
    }

    // 전체 아이템 저장 메서드
    private fun saveAllItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                val pictures = mutableListOf<PictureDto>()
                
                // 현재 표시된 모든 아이템을 PictureDto로 변환
                for (i in 0 until binding.roomCanvas.childCount) {
                    val view = binding.roomCanvas.getChildAt(i)
                    Log.d("view test", "View at $i: ${view.javaClass.simpleName}, tag: ${view.tag}")
                    
                    if (view is FrameLayout) {
                        val item = view.tag as? MyRoomItem
                        Log.d("view test", "Item from tag: $item")
                        
                        if (item?.pictureId != null) {
                            pictures.add(PictureDto(
                                pictureId = item.pictureId!!,
                                userId = userId,
                                imageUrl = item.imageUrl,
                                description = "",
                                rotation = view.rotation.toInt(),
                                furniture = item.id,
                                xval = view.x,
                                yval = view.y,
                                displayed = true
                            ))
                            Log.d("view test", "Added picture: ${pictures.last()}")
                        }
                    }
                }
                
                if (pictures.isEmpty()) {
                    Log.d("view test", "No pictures to save")
                    Toast.makeText(context, "저장할 아이템이 없습니다.", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // PictureService를 사용하여 저장
                val response = RetrofitUtil.pictureService.savePictures(userId, pictures)
                
                if (response.isSuccessful) {
                    Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("EditMyroom", "Failed to save: ${response.code()}")
                    Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("EditMyroom", "Error saving items", e)
                Toast.makeText(context, "저장 중 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayPictureWithItem(item: MyRoomItem) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 그림과 액자 아이템을 함께 표시하는 로직
                val containerView = FrameLayout(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    
                    // 액자 아이템 추가
                    val frameView = ImageView(requireContext()).apply {
                        layoutParams = ViewGroup.LayoutParams(250, 250)
                        scaleType = ImageView.ScaleType.FIT_XY
                        Glide.with(requireContext()).load(item.imageUrl).into(this)
                    }
                    
                    // 그림 추가
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
                    tag = item
                    
                    // 드래그 기능 추가
                    setOnTouchListener(createTouchListener())
                }
                
                binding.roomCanvas.addView(containerView)
                
            } catch (e: Exception) {
                Log.e("EditMyroom", "Failed to display picture with frame", e)
            }
        }
    }

    private fun loadAndDisplayItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                val items = RetrofitUtil.myRoomService.getMyRoomItems(userId)
                items.forEach { pictureDto ->
                    val frameUrl = getItemDrawableResource(pictureDto.pictureId)
                    val myRoomItem = MyRoomItem.fromPictureDto(pictureDto, frameUrl)
                    displayPictureWithItem(myRoomItem)
                }
            } catch (e: Exception) {
                Log.e("EditMyroom", "Failed to load items", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
        mainActivity.hideToolBar(false)
    }

    override fun onStop() {
        super.onStop()
        mainActivity.hideBottomNav(true)
        mainActivity.hideToolBar(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 서버에 저장
    private fun savePictureToServer(item: MyRoomItem) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val request = item.toDisplayRequest()
                val response = RetrofitUtil.myRoomService.savePictureDisplay(
                    ApplicationClass.sharedPreferencesUtil.getUser().userId,
                    listOf(request)
                )

                if (!response.isSuccessful) {
                    Log.e("EditMyroom", "Failed to save to server: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("EditMyroom", "Network error", e)
            }
        }
    }

    // 그림을 액자에 적용
    private fun applyPictureToFrame(frame: MyRoomItem, pictureDto: PictureDto) {
        val updatedItem = frame.copy(
            pictureId = pictureDto.pictureId,
            pictureUrl = pictureDto.imageUrl
        )
        
        // 컨테이너 뷰 생성 (액자 + 그림)
        val containerView = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            
            // 액자 추가
            val frameView = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(250, 250)
                scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(requireContext()).load(updatedItem.imageUrl).into(this)
            }
            
            // 그림 추가
            val pictureView = ImageView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(200, 200).apply {
                    gravity = Gravity.CENTER
                    setMargins(25, 25, 25, 25)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                Glide.with(requireContext()).load(pictureDto.imageUrl).into(this)
            }
            
            addView(frameView)
            addView(pictureView)
            x = updatedItem.position.x
            y = updatedItem.position.y
            rotation = updatedItem.rotation
            tag = updatedItem
            
            setOnTouchListener(createTouchListener())
        }
        
        // 기존 뷰 교체
        val oldView = binding.roomCanvas.findViewWithTag<View>(frame)
        val index = binding.roomCanvas.indexOfChild(oldView)
        binding.roomCanvas.removeView(oldView)
        binding.roomCanvas.addView(containerView, index)
        
        // 서버에 저장
        savePictureToServer(updatedItem)
    }

    private fun setupItemSelection() {
        // 아이템 선택 관련 초기화 코드
        binding.btnEditRoom.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun setupPictureSelection() {
        // 그림 선택 관련 초기화 코드
        binding.albumButtonJw.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .add(android.R.id.content, DrawingAlbumFragment_jw.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    // ItemGridAdapter를 Fragment 내부 클래스로 이동
    private inner class ItemGridAdapter(
        private val items: List<MyItemDto>,
        private val onItemClick: (MyItemDto) -> Unit
    ) : RecyclerView.Adapter<ItemGridAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.item_image)
            val textView: TextView = view.findViewById(R.id.item_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_grid, parent, false)
            
            // 아이템 크기를 화면 너비의 1/3로 설정
            val size = (parent.width - (parent.paddingLeft + parent.paddingRight)) / 3
            view.layoutParams = ViewGroup.LayoutParams(size, size)
            
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            
            // lifecycleScope 대신 viewLifecycleOwner.lifecycleScope 사용
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val imageUrl = getItemDrawableResource(item.itemId)
                    
                    Glide.with(holder.itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.store_item_box_js)
                        .error(R.drawable.store_item_box_js)
                        .into(holder.imageView)
                } catch (e: Exception) {
                    Log.e("ItemGridAdapter", "Failed to load image", e)
                }
            }
            
            holder.textView.text = item.itemName
            holder.itemView.setOnClickListener { onItemClick(item) }
        }

        override fun getItemCount() = items.size
    }

}

// GuestBookAdapter 클래스 수정
class GuestBookAdapter(
    private val guestBooks: List<GuestBook>,
    private val onGuestBookUpdated: (List<GuestBook>) -> Unit
) : RecyclerView.Adapter<GuestBookAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val visitorName: TextView = view.findViewById(R.id.text_visitor_name)
        val message: TextView = view.findViewById(R.id.text_message)
        val date: TextView = view.findViewById(R.id.text_date)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        val btnLike: ImageButton = view.findViewById(R.id.btn_like)
        val likeCount: TextView = view.findViewById(R.id.text_like_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guestbook, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guestBook = guestBooks[position]
        holder.apply {
            visitorName.text = guestBook.visitorName
            message.text = guestBook.message
            date.text = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                .format(Date(guestBook.date))
            ratingBar.rating = guestBook.rating
            likeCount.text = guestBook.likeCount.toString()
            btnLike.setImageResource(
                if (guestBook.isLiked) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )

            btnLike.setOnClickListener {
                val updatedGuestBook = guestBook.copy(
                    isLiked = !guestBook.isLiked,
                    likeCount = guestBook.likeCount + (if (!guestBook.isLiked) 1 else -1)
                )
                val newList = guestBooks.toMutableList().apply {
                    set(position, updatedGuestBook)
                }
                onGuestBookUpdated(newList)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = guestBooks.size
}